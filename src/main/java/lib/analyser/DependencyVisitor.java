package lib.analyser;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.ImportDeclaration;
import lib.report.ClassDepsReport;
import lib.utils.TypeDependency;

import java.util.*;

import static lib.utils.TypeDependency.DependencyType.*;

public class DependencyVisitor extends VoidVisitorAdapter<Void> {
    private final ClassDepsReport report;
    private final String sourceClassName;
    private final Set<String> excludedPackages;

    public DependencyVisitor(ClassDepsReport report, String sourceClassName) {
        this.report = report;
        this.sourceClassName = sourceClassName;
        this.excludedPackages = new HashSet<>(Arrays.asList(
                "java.lang", "java.util", "java.io", "java.math",
                "java.time", "java.text", "java.nio", "java.net"
        ));
    }

    @Override
    public void visit(ImportDeclaration n, Void arg) {
        // Analizza le dipendenze importate
        String importedName = n.getNameAsString();
        if (!n.isStatic() && !importedName.endsWith("*") && shouldExcludeType(importedName)) {
            report.addDependency(new TypeDependency(
                    sourceClassName, importedName, IMPORT,
                    "import " + importedName + ";",
                    n.getBegin().map(pos -> pos.line).orElse(-1)
            ));
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        // Analizza classi o interfacce estese/implementate
        for (ClassOrInterfaceType extendedType : n.getExtendedTypes()) {
            addDependency(extendedType, EXTENDS, "extends");
        }
        for (ClassOrInterfaceType implementedType : n.getImplementedTypes()) {
            addDependency(implementedType, IMPLEMENTS, "implements");
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(FieldDeclaration n, Void arg) {
        // Analizza i campi dichiarati
        for (VariableDeclarator variable : n.getVariables()) {
            if (variable.getType().isClassOrInterfaceType()) {
                addDependency(variable.getType().asClassOrInterfaceType(), FIELD, "field");
            }
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(MethodDeclaration n, Void arg) {
        // Analizza il tipo di ritorno e i parametri
        addDependency(n.getType(), METHOD_RETURN, "return type");
        for (Parameter parameter : n.getParameters()) {
            addDependency(parameter.getType(), METHOD_PARAMETER, "parameter");
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(ObjectCreationExpr n, Void arg) {
        // Analizza le istanze create
        addDependency(n.getType(), INSTANTIATION, "new");
        super.visit(n, arg);
    }

    private void addDependency(Type type, TypeDependency.DependencyType dependencyType, String description) {
        try {
            String typeName = resolveTypeName(type);
            if (shouldExcludeType(typeName)) {
                report.addDependency(new TypeDependency(
                        sourceClassName, typeName, dependencyType,
                        description + " " + type,
                        type.getBegin().map(pos -> pos.line).orElse(-1)
                ));
            }
        } catch (Exception ignored) {
        }
    }

    private String resolveTypeName(Type type) {
        try {
            if (type.isClassOrInterfaceType()) {
                return type.resolve().asReferenceType().getQualifiedName();
            }
        } catch (Exception ignored) {
        }
        return type.asString();
    }

    private boolean shouldExcludeType(String typeName) {
        if (typeName == null || typeName.isEmpty() || typeName.equals("void")) {
            return false;
        }
        for (String excludedPackage : excludedPackages) {
            if (typeName.startsWith(excludedPackage)) {
                return false;
            }
        }
        return !typeName.equals(sourceClassName);
    }
}
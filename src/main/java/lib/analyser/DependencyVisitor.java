package lib.analyser;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.ImportDeclaration;
import lib.report.ClassDepsReport;
import lib.utils.AsyncUtils;
import java.util.*;
import static lib.utils.AsyncUtils.DependencyType.*;
/**
 * A visitor for analyzing dependencies in Java classes.
 * This class traverses the Abstract Syntax Tree (AST) of a Java class and identifies
 * various types of dependencies, such as imports, inheritance, field types, method parameters,
 * return types, and object instantiations.
 */
public class DependencyVisitor extends VoidVisitorAdapter<Void> {
    private final ClassDepsReport report;
    private final String sourceClassName;
    private final Set<String> excludedPackages;
    /**
     * Constructs a new DependencyVisitor.
     *
     * @param report           the ClassDepsReport object to store the dependencies
     * @param sourceClassName  the name of the source class being analyzed
     */
    public DependencyVisitor(ClassDepsReport report, String sourceClassName) {
        this.report = report;
        this.sourceClassName = sourceClassName;
        this.excludedPackages = new HashSet<>(Arrays.asList(
                "java.lang", "java.util", "java.io", "java.math",
                "java.time", "java.text", "java.nio", "java.net"
        ));
    }
    /**
     * Visits an import declaration and adds it as a dependency if it is not excluded.
     *
     * @param n   the ImportDeclaration node
     * @param arg additional argument (not used)
     */ 
    @Override
    public void visit(ImportDeclaration n, Void arg) {
        // Analizza le dipendenze importate
        String importedName = n.getNameAsString();
        if (!n.isStatic() && !importedName.endsWith("*") && shouldExcludeType(importedName)) {
            report.addDependency(new AsyncUtils(
                    sourceClassName, importedName, IMPORT,
                    "import " + importedName + ";",
                    n.getBegin().map(pos -> pos.line).orElse(-1)
            ));
        }
        super.visit(n, arg);
    }
    /**
     * Visits a class or interface declaration and analyzes its inheritance and implemented interfaces.
     *
     * @param n   the ClassOrInterfaceDeclaration node
     * @param arg additional argument (not used)
     */
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
    /**
     * Visits a field declaration and analyzes its type.
     *
     * @param n   the FieldDeclaration node
     * @param arg additional argument (not used)
     */
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
    /**
     * Visits a method declaration and analyzes its return type and parameters.
     *
     * @param n   the MethodDeclaration node
     * @param arg additional argument (not used)
     */
    @Override
    public void visit(MethodDeclaration n, Void arg) {
        // Analizza il tipo di ritorno e i parametri
        addDependency(n.getType(), METHOD_RETURN, "return type");
        for (Parameter parameter : n.getParameters()) {
            addDependency(parameter.getType(), METHOD_PARAMETER, "parameter");
        }
        super.visit(n, arg);
    }
    /**
     * Visits an object creation expression and analyzes the instantiated type.
     *
     * @param n   the ObjectCreationExpr node
     * @param arg additional argument (not used)
     */
    @Override
    public void visit(ObjectCreationExpr n, Void arg) {
        // Analizza le istanze create
        addDependency(n.getType(), INSTANTIATION, "new");
        super.visit(n, arg);
    }
    /**
     * Adds a dependency to the report if it is not excluded.
     *
     * @param type           the type being analyzed
     * @param dependencyType the type of dependency (e.g., IMPORT, EXTENDS)
     * @param description    a description of the dependency
     */
    private void addDependency(Type type, AsyncUtils.DependencyType dependencyType, String description) {
        try {
            String typeName = resolveTypeName(type);
            if (shouldExcludeType(typeName)) {
                report.addDependency(new AsyncUtils(
                        sourceClassName, typeName, dependencyType,
                        description + " " + type,
                        type.getBegin().map(pos -> pos.line).orElse(-1)
                ));
            }
        } catch (Exception ignored) {
            // Ignora eventuali errori di risoluzione del tipo
        }
    }
    /**
     * Resolves the fully qualified name of a type.
     *
     * @param type the type to resolve
     * @return the fully qualified name of the type, or its simple name if resolution fails
     */
    private String resolveTypeName(Type type) {
        try {
            if (type.isClassOrInterfaceType()) {
                return type.resolve().asReferenceType().getQualifiedName();
            }
        } catch (Exception ignored) {
        }
        return type.asString();
    }
    /**
     * Determines whether a type should be excluded based on its name.
     *
     * @param typeName the name of the type to check
     * @return true if the type should be excluded, false otherwise
     */
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
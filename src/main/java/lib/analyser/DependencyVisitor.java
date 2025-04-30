package lib.analyser;

import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import lib.report.ClassDepsReport;
import com.github.javaparser.ast.body.MethodDeclaration;


public class DependencyVisitor extends VoidVisitorAdapter<ClassDepsReport> {
    private final String className;

    public DependencyVisitor(ClassDepsReport report, String className) {
        this.className = className;
    }

    @Override
    public void visit(MethodDeclaration md, ClassDepsReport report) {
        super.visit(md, report);
        // Logica per analizzare le dipendenze nei metodi
    }
}
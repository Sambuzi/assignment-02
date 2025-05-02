package lib.analyser;

import io.vertx.core.*;

import java.nio.file.Path;

public class DependencyAnalyserVerticle extends AbstractVerticle {

    private static final String CURRENT_PATH = System.getProperty("user.dir");
    private static final Path CLASS_PATH = Path.of(CURRENT_PATH, "src", "main", "java", "lib", "report", "ClassDepsReport.java");
    private static final Path PACKAGE_PATH = Path.of(CURRENT_PATH, "src", "main", "java", "lib", "report");
    private static final Path PROJECT_PATH = Path.of(CURRENT_PATH);

    @Override
    public void start(Promise<Void> startPromise) {
        final DependencyAnalyserLib dependencyAnalyser = new DependencyAnalyserLib(this.vertx);

        // Analisi incrementale: prima classe, poi pacchetto, poi progetto
        dependencyAnalyser.getClassDependencies(CLASS_PATH)
                .compose(classReport -> {
                    System.out.println("=== Class Report ===");
                    System.out.println(classReport);
                    return dependencyAnalyser.getPackageDependencies(PACKAGE_PATH);
                })
                .compose(packageReport -> {
                    System.out.println("=== Package Report ===");
                    System.out.println(packageReport);
                    return dependencyAnalyser.getProjectDependencies(PROJECT_PATH);
                })
                .onSuccess(projectReport -> {
                    System.out.println("=== Project Report ===");
                    System.out.println(projectReport);
                    startPromise.complete();
                })
                .onFailure(err -> {
                    System.err.println("Error: " + err.getMessage());
                    startPromise.fail(err);
                });
    }
}
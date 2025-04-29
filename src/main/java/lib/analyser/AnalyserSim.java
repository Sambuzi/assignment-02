package lib.analyser;

import io.vertx.core.Vertx;

import java.nio.file.Path;

public class AnalyserSim {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        DependencyAnalyserLib analyser = new DependencyAnalyserLib(vertx);

        analyser.getProjectDependencies(Path.of("src/main/java")) //to fix , need to add reports file
                .onSuccess(report -> System.out.println(report))
                .onFailure(err -> System.err.println("Error: " + err.getMessage()));
    }
}
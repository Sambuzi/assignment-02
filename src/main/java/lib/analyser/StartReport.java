package lib.analyser;

import io.vertx.core.Vertx;

/**
 * Main class for running the DependencyAnalyserVerticle.
 * This class initializes the Vert.x framework, deploys the DependencyAnalyserVerticle,
 * and ensures proper cleanup of resources.
 */
public class StartReport {
    public static void main(String[] args) {
        // Initialize Vert.x
        Vertx vertx = Vertx.vertx();

        // Deploy the DependencyAnalyserVerticle
        vertx.deployVerticle(new DependencyAnalyserVerticle(), result -> {
            if (result.succeeded()) {
                System.out.println("DependencyAnalyserVerticle deployed successfully!");
            } else {
                System.err.println("Failed to deploy DependencyAnalyserVerticle: " + result.cause().getMessage());
            }

            // Close Vert.x after deployment (success or failure)
            vertx.close();
        });
    }
}
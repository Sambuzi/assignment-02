package lib.analyser;
import io.vertx.core.Vertx;
/**
 * Main class for running the DependencyAnalyserVerticle.
 * This class initializes the Vert.x framework and deploys the DependencyAnalyserVerticle.
 */
public class SimulationAnalyser {
    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new DependencyAnalyserVerticle())
                .onComplete(r -> vertx.close());
    }
}
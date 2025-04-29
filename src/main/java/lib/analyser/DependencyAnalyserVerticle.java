package lib.analyser;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class DependencyAnalyserVerticle extends AbstractVerticle {
    private DependencyAnalyserLib analyser;

    @Override
    public void start(Promise<Void> startPromise) {
        analyser = new DependencyAnalyserLib(vertx);
        System.out.println("DependencyAnalyserVerticle started!");
        startPromise.complete();
    }

    public DependencyAnalyserLib getAnalyser() {
        return analyser;
    }
}
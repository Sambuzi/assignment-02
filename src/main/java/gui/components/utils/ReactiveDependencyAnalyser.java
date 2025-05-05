package gui.components.utils;

import io.reactivex.rxjava3.core.Observable;
import java.io.File;
import java.nio.file.Path;

public class ReactiveDependencyAnalyser {

    /**
     * Analizza i file .java in una directory e restituisce un flusso reattivo di dipendenze.
     * @param projectPath Percorso della directory del progetto.
     * @return Observable che emette le dipendenze trovate.
     */
    public Observable<String[]> analyzeDependencies(Path projectPath) {
        return Observable.create(emitter -> {
            File folder = projectPath.toFile();
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".java"));

            if (files == null || files.length == 0) {
                emitter.onError(new Exception("No Java files found in the directory."));
                return;
            }

            for (File file : files) {
                try {
                    // Simulazione analisi dipendenze
                    Thread.sleep(500);
                    String className = file.getName().replace(".java", "");
                    emitter.onNext(new String[]{className, "Dependency1", "Dependency2"});
                } catch (InterruptedException e) {
                    emitter.onError(e);
                }
            }

            emitter.onComplete();
        });
    }
}

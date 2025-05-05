package gui.components.utils;

import io.reactivex.rxjava3.core.Observable;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ReactiveDependencyAnalyser {

    public Observable<String[]> analyzeDependencies(Path projectPath) {
        return Observable.create(emitter -> {
            File folder = projectPath.toAbsolutePath().toFile();
            if (!folder.exists() || !folder.isDirectory()) {
                emitter.onError(new Exception("Invalid directory: " + projectPath));
                return;
            }

            List<File> javaFiles = new ArrayList<>();
            findJavaFilesRecursively(folder, javaFiles);

            if (javaFiles.isEmpty()) {
                emitter.onError(new Exception("No Java files found in the directory: " + projectPath));
                return;
            }

            for (File file : javaFiles) {
                try {
                    System.out.println("Found file: " + file.getName());

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

    private void findJavaFilesRecursively(File directory, List<File> javaFiles) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                findJavaFilesRecursively(file, javaFiles);
            } else if (file.getName().endsWith(".java")) {
                javaFiles.add(file);
            }
        }
    }
}

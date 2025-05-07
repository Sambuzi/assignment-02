package gui.components.utils;

import io.reactivex.rxjava3.core.Observable;

import java.io.*;
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
                    Thread.sleep(300); // per simulare tempo di analisi

                    String className = extractFullClassName(file);
                    List<String> dependencies = extractImports(file);

                    List<String> output = new ArrayList<>();
                    output.add(className);
                    output.addAll(dependencies);

                    emitter.onNext(output.toArray(new String[0]));
                } catch (Exception e) {
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

    private List<String> extractImports(File file) throws IOException {
        List<String> imports = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("import ") && line.endsWith(";")) {
                    String imported = line.substring(7, line.length() - 1); // rimuove "import " e ";"
                    // Optional: filtra solo classi del progetto se necessario
                    imports.add(imported);
                }
            }
        }
        return imports;
    }
    private String extractFullClassName(File file) throws IOException {
        String className = file.getName().replace(".java", "");
        String packageName = "default";
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("package ")) {
                    packageName = line.substring(8, line.indexOf(';')).trim();
                    break;
                }
            }
        }
        return packageName + "." + className;
    }
    
}

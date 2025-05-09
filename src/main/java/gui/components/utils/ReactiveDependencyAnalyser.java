package gui.components.utils;

import io.reactivex.rxjava3.core.Observable;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * ReactiveDependencyAnalyser is a utility class that analyzes Java files in a project directory
 * and extracts dependencies (imports) and class names using a reactive programming approach.
 * It emits the results incrementally as an RxJava Observable.
 */
public class ReactiveDependencyAnalyser {

    /**
     * Analyzes the dependencies of all Java files in the given project directory.
     *
     * @param projectPath The path to the project directory.
     * @return An Observable that emits an array of strings for each Java file.
     *         The first element is the fully qualified class name, followed by its dependencies (imports).
     */
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
                    Thread.sleep(300); // Simulate analysis time

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

    /**
     * Recursively finds all Java files in the given directory and its subdirectories.
     *
     * @param directory The directory to search.
     * @param javaFiles The list to store the found Java files.
     */
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

    /**
     * Extracts the import statements from a Java file.
     *
     * @param file The Java file to analyze.
     * @return A list of fully qualified class names from the import statements.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    private List<String> extractImports(File file) throws IOException {
        List<String> imports = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("import ") && line.endsWith(";")) {
                    String imported = line.substring(7, line.length() - 1); // Remove "import " and ";"
                    imports.add(imported);
                }
            }
        }
        return imports;
    }

    /**
     * Extracts the fully qualified class name from a Java file.
     *
     * @param file The Java file to analyze.
     * @return The fully qualified class name (package name + class name).
     * @throws IOException If an I/O error occurs while reading the file.
     */
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
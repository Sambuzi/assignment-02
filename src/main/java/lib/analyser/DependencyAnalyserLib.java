package lib.analyser;

import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.*;
import io.vertx.core.*;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import lib.report.*;

/**
 * A library for analyzing dependencies in Java projects.
 * Provides asynchronous methods to analyze dependencies at the class, package, and project levels.
 */
public class DependencyAnalyserLib {
    private final Vertx vertx;
    private final JavaParser parser;

    /**
     * Constructs a DependencyAnalyserLib instance.
     *
     * @param vertx The Vert.x instance used for asynchronous operations.
     */
    public DependencyAnalyserLib(Vertx vertx) {
        this.vertx = vertx;
        this.parser = createJavaParser();
    }

    // -------------------------------
    // Public API
    // -------------------------------

    /**
     * Analyzes the dependencies of a single Java class.
     *
     * @param classSrcFile The path to the Java source file.
     * @return A future containing the class dependency report.
     */
    public Future<ClassDepsReport> getClassDependencies(Path classSrcFile) {
        return readFileAsync(classSrcFile).compose(this::analyzeClassDependencies);
    }

    /**
     * Analyzes the dependencies of all classes in a package.
     *
     * @param packageSrcFolder The path to the package folder.
     * @return A future containing the package dependency report.
     */
    public Future<PackageDepsReport> getPackageDependencies(Path packageSrcFolder) {
        return processFilesInFolder(packageSrcFolder, this::getClassDependencies, PackageDepsReport::new);
    }

    /**
     * Analyzes the dependencies of all packages in a project.
     *
     * @param projectSrcFolder The path to the project folder.
     * @return A future containing the project dependency report.
     */
    public Future<ProjectDepsReport> getProjectDependencies(Path projectSrcFolder) {
        String projectName = projectSrcFolder.getFileName() != null
                ? projectSrcFolder.getFileName().toString()
                : "UnknownProject";

        List<Path> packageDirs = findPackageDirectories(projectSrcFolder);
        return processFolders(packageDirs, this::getPackageDependencies, name -> new ProjectDepsReport(projectName));
    }

    // -------------------------------
    // Private Helpers
    // -------------------------------

    /**
     * Processes all Java files in a folder and generates a report.
     *
     * @param folder           The folder containing Java files.
     * @param processor        A function to process each file.
     * @param reportConstructor A function to construct the report.
     * @param <T>              The type of the result for each file.
     * @param <R>              The type of the final report.
     * @return A future containing the report.
     */
    private <T, R> Future<R> processFilesInFolder(Path folder, Function<Path, Future<T>> processor, Function<String, R> reportConstructor) {
        Promise<R> promise = Promise.promise();
        File[] files = listFiles(folder, ".java");
        if (files == null || files.length == 0) {
            promise.complete(reportConstructor.apply(folder.getFileName() != null ? folder.getFileName().toString() : "UnknownFolder"));
            return promise.future();
        }

        List<Future> futures = new ArrayList<>();
        for (File file : files) futures.add(processor.apply(file.toPath()));

        CompositeFuture.all(futures).onSuccess(result -> {
            R report = reportConstructor.apply(folder.getFileName() != null ? folder.getFileName().toString() : "UnknownFolder");
            for (int i = 0; i < result.size(); i++) addToReport(report, result.resultAt(i));
            promise.complete(report);
        }).onFailure(promise::fail);

        return promise.future();
    }

    /**
     * Processes all folders and generates a report.
     *
     * @param folders          The list of folders to process.
     * @param processor        A function to process each folder.
     * @param reportConstructor A function to construct the report.
     * @param <T>              The type of the result for each folder.
     * @param <R>              The type of the final report.
     * @return A future containing the report.
     */
    private <T, R> Future<R> processFolders(List<Path> folders, Function<Path, Future<T>> processor, Function<String, R> reportConstructor) {
        Promise<R> promise = Promise.promise();
        List<Future> futures = new ArrayList<>();
        for (Path folder : folders) {
            if (folder == null || folder.getFileName() == null) {
                System.err.println("Invalid folder path: " + folder);
                continue; // Skip invalid paths
            }
            futures.add(processor.apply(folder));
        }

        CompositeFuture.all(futures).onSuccess(result -> {
            String parentName = folders.get(0).getParent() != null && folders.get(0).getParent().getFileName() != null
                    ? folders.get(0).getParent().getFileName().toString()
                    : "UnknownParent";
            R report = reportConstructor.apply(parentName);
            for (int i = 0; i < result.size(); i++) addToReport(report, result.resultAt(i));
            promise.complete(report);
        }).onFailure(promise::fail);

        return promise.future();
    }

    /**
     * Adds an item to the report.
     *
     * @param report The report to update.
     * @param item   The item to add.
     * @param <R>    The type of the report.
     * @param <T>    The type of the item.
     */
    private <R, T> void addToReport(R report, T item) {
        if (report instanceof PackageDepsReport && item instanceof ClassDepsReport) {
            PackageDepsReport packageReport = (PackageDepsReport) report;
            ClassDepsReport classReport = (ClassDepsReport) item;
            if (!classReport.getDependencies().isEmpty()) {
                packageReport.addClassReport(classReport);
            }
        } else if (report instanceof ProjectDepsReport && item instanceof PackageDepsReport) {
            ProjectDepsReport projectReport = (ProjectDepsReport) report;
            PackageDepsReport packageReport = (PackageDepsReport) item;
            if (!packageReport.getClassReports().isEmpty()) {
                projectReport.addPackageReport(packageReport);
            }
        }
    }

    /**
     * Reads a file asynchronously.
     *
     * @param filePath The path to the file.
     * @return A future containing the file content as a string.
     */
    private Future<String> readFileAsync(Path filePath) {
        Promise<String> promise = Promise.promise();
        vertx.fileSystem().readFile(filePath.toString(), result -> {
            if (result.succeeded()) promise.complete(result.result().toString("UTF-8"));
            else promise.fail("Error reading file: " + result.cause().getMessage());
        });
        return promise.future();
    }

    /**
     * Analyzes the dependencies of a class from its source code.
     *
     * @param sourceCode The source code of the class.
     * @return A future containing the class dependency report.
     */
    private Future<ClassDepsReport> analyzeClassDependencies(String sourceCode) {
        Promise<ClassDepsReport> promise = Promise.promise();
        try {
            CompilationUnit cu = parser.parse(sourceCode).getResult().orElseThrow();
            String className = getClassName(cu);
            ClassDepsReport classReport = new ClassDepsReport(className);
            cu.accept(new DependencyVisitor(classReport, className), null);
            promise.complete(classReport);
        } catch (Exception e) {
            promise.fail("Failed to analyze class: " + e.getMessage());
        }
        return promise.future();
    }

    /**
     * Lists all files in a folder with a specific extension.
     *
     * @param folder    The folder to search.
     * @param extension The file extension to filter by.
     * @return An array of files matching the extension.
     */
    private File[] listFiles(Path folder, String extension) {
        return folder.toFile().listFiles((dir, name) -> name.endsWith(extension));
    }

    /**
     * Extracts the class name from a compilation unit.
     *
     * @param cu The compilation unit.
     * @return The class name, or "UnknownClass" if not found.
     */
    private String getClassName(CompilationUnit cu) {
        return cu.findFirst(ClassOrInterfaceDeclaration.class)
                .map(ClassOrInterfaceDeclaration::getNameAsString)
                .orElse("UnknownClass");
    }

    /**
     * Creates a JavaParser instance with a symbol resolver.
     *
     * @return The configured JavaParser instance.
     */
    private JavaParser createJavaParser() {
        CombinedTypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver(false));
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        JavaParser parser = new JavaParser();
        parser.getParserConfiguration().setSymbolResolver(symbolSolver);
        return parser;
    }

    /**
     * Finds all package directories in a project.
     *
     * @param projectDir The project directory.
     * @return A list of package directories.
     */
    private List<Path> findPackageDirectories(Path projectDir) {
        List<Path> packageDirs = new ArrayList<>();
        findPackageDirsRecursive(projectDir.toFile(), packageDirs);
        return packageDirs;
    }

    /**
     * Recursively finds package directories containing Java files.
     *
     * @param dir         The directory to search.
     * @param packageDirs The list to store package directories.
     */
    private void findPackageDirsRecursive(File dir, List<Path> packageDirs) {
        if (listFiles(dir.toPath(), ".java") != null) packageDirs.add(dir.toPath());
        File[] subDirs = dir.listFiles(File::isDirectory);
        if (subDirs != null) {
            for (File subDir : subDirs) findPackageDirsRecursive(subDir, packageDirs);
        }
    }
}
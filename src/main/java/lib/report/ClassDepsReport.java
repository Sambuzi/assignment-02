package lib.report;

import java.util.HashSet;
import java.util.Set;

public class ClassDepsReport {
    private final String className;
    private final Set<String> dependencies = new HashSet<>();

    public ClassDepsReport(String className) {
        this.className = className;
    }

    public void addDependency(String dependency) {
        dependencies.add(dependency);
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return "Class: " + className + ", Dependencies: " + dependencies;
    }
}
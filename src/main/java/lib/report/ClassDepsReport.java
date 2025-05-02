package lib.report;

import java.util.HashSet;
import java.util.Set;

import lib.utils.TypeDependency;

public class ClassDepsReport {
    private final String className;
    private final Set<TypeDependency> dependencies = new HashSet<>();

    public ClassDepsReport(String className) {
        this.className = className;
    }

    public void addDependency(TypeDependency dependency) {
        dependencies.add(dependency);
    }

    public Set<TypeDependency> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Class:").append(className).append("\n");
        sb.append("Dependencies:\n");
        for (TypeDependency dep : dependencies) {
            sb.append("  - ").append(dep.getDependencyType()).append(": ").append(dep.getTargetType()).append("\n");
        }
        return sb.toString();
    }
}
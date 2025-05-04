package lib.report;

import java.util.HashSet;
import java.util.Set;
import lib.utils.AsyncUtils;
/**
 * A report class for storing dependencies of a Java class.
 * This class contains the class name and a set of dependencies associated with it.
 * It also provides methods to add dependencies and retrieve them.
 * This class also overrides the toString method to provide a string representation of the report.
 * This class also includes a method to get the class name.
 * @return the class name of the report.
 */
public class ClassDepsReport {
    private final String className;
    private final Set<AsyncUtils> dependencies = new HashSet<>();
    /**
     * Constructs a new ClassDepsReport for the specified class.
     *
     * @param className the name of the class being analyzed
     */
    public ClassDepsReport(String className) {
        this.className = className;
    }
    /**
     * Adds a dependency to the report.
     *
     * @param dependency the dependency to add
     */
    public void addDependency(AsyncUtils dependency) {
        dependencies.add(dependency);
    }
    /**
     * Retrieves the set of dependencies associated with the class.
     *
     * @return a set of TypeDependency objects representing the dependencies
     */
    public Set<AsyncUtils> getDependencies() {
        return dependencies;
    }
    /**
     * Generates a string representation of the class dependencies report.
     * The output includes the class name and a list of its dependencies.
     *
     * @return a formatted string representation of the report
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Class:").append(className).append("\n");
        sb.append("Dependencies:\n");
        for (AsyncUtils dep : dependencies) {
            sb.append("  - ").append(dep.getDependencyType()).append(": ").append(dep.getTargetType()).append("\n");
        }
        return sb.toString();
    }
}
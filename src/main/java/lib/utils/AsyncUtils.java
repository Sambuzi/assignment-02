package lib.utils;

import java.util.*;

/**
 * Represents a dependency between two types in a Java project.
 */
public class AsyncUtils {

    public enum DependencyType {
        IMPORT,
        EXTENDS,
        IMPLEMENTS,
        INSTANTIATION,
        FIELD,
        METHOD_PARAMETER,
        METHOD_RETURN
    }

    private final String sourceType;
    private final String targetType;
    private final DependencyType dependencyType;
    private final String codeSnippet;
    private final int lineNumber;
    /**
     * Constructs a new TypeDependency with the specified parameters.
     *
     * @param sourceType the name of the source class
     * @param targetType the name of the dependent class
     * @param dependencyType the type of dependency (e.g., IMPORT, EXTENDS)
     * @param codeSnippet a preview of the code that generates the dependency
     * @param lineNumber the line number where the dependency is found
     */
    public AsyncUtils(String sourceType, String targetType, DependencyType dependencyType, String codeSnippet, int lineNumber) {
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.dependencyType = dependencyType;
        this.codeSnippet = codeSnippet;
        this.lineNumber = lineNumber;
    }

    /**
     * Retrieves the name of the source class.
     *
     * @return the name of the source class
     */

    public String getSourceType() {
        return sourceType;
    }

    /**
     * Retrieves the name of the target class.
     *
     * @return the name of the target class
     */
    public String getTargetType() {
        return targetType;
    }
    /**
     * Retrieves the type of dependency.
     *
     * @return the type of dependency (e.g., IMPORT, EXTENDS)
     */
    public DependencyType getDependencyType() {
        return dependencyType;
    }
    /**
     * Retrieves the code snippet that generates the dependency.
     *
     * @return a preview of the code that generates the dependency
     */
    public String getCodeSnippet() {
        return codeSnippet;
    }
    /**
     * Retrieves the line number where the dependency is found.
     *
     * @return the line number where the dependency is found
     */
    public int getLineNumber() {
        return lineNumber;
    }
    /**
     * Checks if the dependency has a line number associated with it.
     *
     * @return true if the dependency has a line number, false otherwise
     */
    public boolean hasLineNumber() {
        return lineNumber > 0;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AsyncUtils that = (AsyncUtils) o;
        return lineNumber == that.lineNumber &&
                Objects.equals(sourceType, that.sourceType) &&
                Objects.equals(targetType, that.targetType) &&
                dependencyType == that.dependencyType &&
                Objects.equals(codeSnippet, that.codeSnippet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceType, targetType, dependencyType, codeSnippet, lineNumber);
    }
    /**
     * Generates a string representation of the TypeDependency object.
     * The output includes the source type, target type, dependency type, and line number (if available).
     *
     * @return a formatted string representation of the TypeDependency object
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(sourceType)
                .append(" -> ")
                .append(targetType)
                .append(" (")
                .append(dependencyType);

        if (codeSnippet != null && hasLineNumber()) {
            sb.append(": ").append(codeSnippet);
            sb.append(" at line: ").append(lineNumber);
        }
        sb.append(")");
        return sb.toString();
    }
}
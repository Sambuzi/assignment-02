package lib.utils;

import java.util.*;

/**
 * Represents a dependency between two types in a Java project.
 */
public class TypeDependency {

    public enum DependencyType {
        IMPORT,
        EXTENDS,
        IMPLEMENTS,
        INSTANTIATION,
        FIELD,
        METHOD_PARAMETER,
        METHOD_RETURN
    }

    private final String sourceType; // Nome della classe sorgente
    private final String targetType; // Nome della classe dipendente
    private final DependencyType dependencyType; // Tipo di dipendenza (es. IMPORT, EXTENDS)
    private final String codeSnippet; // Anteprima del codice che genera la dipendenza
    private final int lineNumber; // Numero di riga in cui si trova la dipendenza

    public TypeDependency(String sourceType, String targetType, DependencyType dependencyType, String codeSnippet, int lineNumber) {
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.dependencyType = dependencyType;
        this.codeSnippet = codeSnippet;
        this.lineNumber = lineNumber;
    }

    // Getter per il nome della classe sorgente
    public String getSourceType() {
        return sourceType;
    }

    // Getter per il nome della classe dipendente
    public String getTargetType() {
        return targetType;
    }

    // Getter per il tipo di dipendenza
    public DependencyType getDependencyType() {
        return dependencyType;
    }

    // Getter per l'anteprima del codice
    public String getCodeSnippet() {
        return codeSnippet;
    }

    // Getter per il numero di riga
    public int getLineNumber() {
        return lineNumber;
    }

    // Verifica se è presente un numero di riga valido
    public boolean hasLineNumber() {
        return lineNumber > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeDependency that = (TypeDependency) o;
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
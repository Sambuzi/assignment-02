package lib.utils;

public class TypeDependency {
    private final String typeName;

    public TypeDependency(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public String toString() {
        return typeName;
    }
}
package de.realityinabox.databinding.types;

public class ArrayType extends PrimitiveType {
    
    private Type elementType;

    public ArrayType(Type elementType) {
        super(elementType.getPackage());
        this.elementType = elementType;
    }

    public String getJavaWrapper() {
        return "java.util.List";
    }

    public boolean structurallyEquals(Type t) {
        if (t instanceof ArrayType) return elementType.structurallyEquals(t);
        return false;
    }

    public String getJavaName() {
        return elementType.getJavaName() + "[]";
    }
}

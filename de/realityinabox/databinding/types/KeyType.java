package de.realityinabox.databinding.types;

import de.realityinabox.databinding.sourcemodel.ClassType;

public class KeyType extends Type {
    
    private Type actualType;
    private String keyName;

    protected KeyType(Type actualType, String keyName) {
        super(actualType.getPackage());
        this.actualType = actualType;
        this.keyName = keyName;
    }
    
    public Type getActualType() {
        return this.actualType;
    }
    
    public String getKeyName() {
        return this.keyName;
    }

    public boolean structurallyEquals(Type t) {
        if (t instanceof KeyType)
            return actualType.structurallyEquals(((KeyType) t).actualType);
        return actualType.structurallyEquals(t);
    }

    public ClassType getClassType() {
        return actualType.getClassType();
    }

    public String getJavaName() {
        return actualType.getJavaName();
    }

}

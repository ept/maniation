package de.realityinabox.databinding.types;

import de.realityinabox.databinding.sourcemodel.ClassType;
import de.realityinabox.databinding.sourcemodel.Property;

public class KeyRefType extends ReferenceType {
    
    private String keyName;
    private Type actualType;
    private ClassType classType = null;
    private Property keyProperty = null;

    protected KeyRefType(Type actualType, String keyName) {
        super(actualType.getPackage(), null, null);
        this.keyName = keyName;
        this.actualType = actualType;
    }
    
    public Type getActualType() {
        return actualType;
    }
    
    public String getKeyName() {
        return keyName;
    }

    public void setClassType(ClassType classType, Property keyProperty) {
        this.classType = classType;
        this.keyProperty = keyProperty;
    }

    public ClassType getClassType() {
        return this.classType;
    }
    
    public Property getKeyProperty() {
        return keyProperty;
    }
    
    public String getJavaName() {
        if (classType == null) return "__unresolvedKeyReference__";
        return classType.getFullName();
    }

    public void makeProperties(String propertiesName, ClassType container) {
        super.makeProperties(propertiesName.replaceFirst("[^a-zA-Z]+[Ii][Dd]\\Z", ""), container);
    }
}

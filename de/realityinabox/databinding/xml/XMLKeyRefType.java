package de.realityinabox.databinding.xml;

import de.realityinabox.databinding.sourcemodel.ClassType;
import de.realityinabox.databinding.sourcemodel.Method;
import de.realityinabox.databinding.sourcemodel.Package;
import de.realityinabox.databinding.sourcemodel.Property;
import de.realityinabox.databinding.types.KeyRefType;
import de.realityinabox.databinding.types.KeyType;
import de.realityinabox.databinding.types.Type;

public class XMLKeyRefType extends KeyRefType {

    protected XMLKeyRefType(Type actualType, String keyName) {
        super(actualType, keyName);
    }
    
    public void setClassType(ClassType classType, Property keyProperty) {
        super.setClassType(classType, keyProperty);
        Package pack = getPackage();
        Type keyType = keyProperty.getType();
        if ((pack instanceof XMLPackage) && (keyType instanceof KeyType)) {
            ((XMLPackage) pack).getDocument().addKeyLookup((KeyType) keyType, this);
        }
    }

    public String javaConvertToString(String source) {
        if (getKeyProperty() == null) throw new IllegalStateException();
        Method accessor = null;
        for (Method meth : getKeyProperty().getMethods()) {
            if (meth.getReturnType().structurallyEquals(getActualType()) &&
                    (meth.getArguments().size() == 0))
                accessor = meth;
        }
        if (accessor == null) throw new IllegalStateException();
        source = "(" + source + " == null ? \"\" : " + source + "." + accessor.getName() + "())";
        return getActualType().javaConvertToString(source);
    }

    public String javaValidation(String variableName) {
        return super.javaValidation(variableName) + variableName + "UpToDate = true;";
    }
}

package de.realityinabox.databinding.xml;

import de.realityinabox.databinding.sourcemodel.Tools;
import de.realityinabox.databinding.types.KeyType;
import de.realityinabox.databinding.types.Type;

public class XMLKeyType extends KeyType {
    
    protected XMLKeyType(Type actualType, String keyName) {
        super(actualType, keyName);
    }

    public String javaValidation(String variableName) {
        String result = super.javaValidation(variableName);
        String mapping = "_document.map" + Tools.toJavaName(getKeyName(), true) + "Keys";
        result += mapping + ".remove(this." + variableName + "); ";
        result += mapping + ".put(" + variableName + ", this);";
        return result;
    }
}

package de.realityinabox.databinding.types;

import de.realityinabox.databinding.sourcemodel.ClassType;
import de.realityinabox.databinding.sourcemodel.Package;
import de.realityinabox.databinding.sourcemodel.Property;
import de.realityinabox.databinding.sourcemodel.SourceFactory;

public class BooleanType extends PrimitiveType {

    protected BooleanType(Package pack) {
        super(pack);
    }
    
    public boolean structurallyEquals(Type t) {
        return t instanceof BooleanType;
    }
    
    public String getJavaName() {
        return "boolean";
    }

    public String getJavaWrapper() {
        return "java.lang.Boolean";
    }

    public String javaConvertFromString(String source) {
        return source + ".equals(\"1\") || " + source + ".equals(\"true\")";
    }

    public String javaConvertToString(String source) {
        return "(" + source + " ? \"true\" : \"false\")";
    }

    public void makeProperties(String propertiesName, ClassType container) {
        Property p = SourceFactory.newProperty(getPackage(), propertiesName, this);
        p.setGetMethodPrefix("is");
        container.addProperty(p);
    }
}

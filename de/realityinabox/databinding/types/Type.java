package de.realityinabox.databinding.types;

import java.util.Map;
import de.realityinabox.databinding.sourcemodel.ClassType;
import de.realityinabox.databinding.sourcemodel.Package;
import de.realityinabox.databinding.sourcemodel.SourceContext;
import de.realityinabox.databinding.sourcemodel.SourceFactory;

public abstract class Type {

    private Package pack;
    private boolean global = false;
    private Representation representation = Representation.UNKNOWN;
    private Map<String,Object> extendedInfo = new java.util.HashMap<String,Object>();

    protected Type(Package pack) {
        this.pack = pack;
    }

    public Package getPackage() {
        return pack;
    }
    
    public boolean isGlobal() {
        return global;
    }
    
    public void setGlobal(boolean global) {
        this.global = global;
    }
    
    public Representation getRepresentation() {
        return representation;
    }
    
    public void setRepresentation(Representation representation) {
        this.representation = representation;
    }
    
    public Map<String,Object> getExtendedInfo() {
        return extendedInfo;
    }
    
    public String javaValidation(String variableName) {
        return "";
    }
    
    public String javaConvertToString(String source) {
        return source;
    }
    
    public String javaConvertFromString(String source) {
        return source;
    }

    public abstract boolean structurallyEquals(Type t); 
    public abstract ClassType getClassType();
    public abstract String getJavaName();

    public void write(java.io.OutputStream stream, SourceContext context) throws java.io.IOException {
        stream.write(getJavaName().getBytes(context.getCharsetName()));
    }

    public void makeProperties(String propertiesName, ClassType container) {
        container.addProperty(SourceFactory.newProperty(getPackage(), propertiesName, this));
    }
}

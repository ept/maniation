package de.realityinabox.databinding.types;

import de.realityinabox.databinding.sourcemodel.Package;

public class VoidType extends PrimitiveType {

    protected VoidType(Package pack) {
        super(pack);
    }
    
    public boolean structurallyEquals(Type t) {
        return t instanceof VoidType;
    }
    
    public String getJavaName() {
        return "void";
    }

    public String getJavaWrapper() {
        return "java.lang.Void";
    }
}

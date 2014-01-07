package de.realityinabox.databinding.types;

import de.realityinabox.databinding.sourcemodel.Package;

public class IntegralType extends NumericType {
    
    public enum Size {
        BYTE ("byte", "java.lang.Byte"), SHORT ("short", "java.lang.Short"),
        INT ("int", "java.lang.Integer"), LONG ("long", "java.lang.Long"),
        CHAR ("char", "java.lang.Character");
        
        private final String javaName;
        private final String javaWrapper;
        
        Size(String javaName, String javaWrapper) {
            this.javaName = javaName;
            this.javaWrapper = javaWrapper;
        }
        public String getJavaName() {
            return this.javaName;
        }
        public String getJavaWrapper() {
            return this.javaWrapper;
        }
    }

    private Size size;

    protected IntegralType(Package pack, Size size) {
        super(pack);
        this.size = size;
    }

    public Size getSize() {
        return this.size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public boolean structurallyEquals(Type t) {
        if (t instanceof IntegralType) {
            return ((IntegralType) t).size == this.size;
        }
        return false;
    }

   public String getJavaName() {
        return this.size.getJavaName();
    }

    public String getJavaWrapper() {
        return this.size.getJavaWrapper();
    }
}

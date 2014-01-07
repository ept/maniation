package de.realityinabox.databinding.types;

import de.realityinabox.databinding.sourcemodel.Package;

public class FloatingPointType extends NumericType {

    public enum Size {
        SINGLE ("float", "java.lang.Float", "parseFloat", "toString"),
        DOUBLE ("double", "java.lang.Double", "parseDouble", "toString");

        private final String javaName;
        private final String javaWrapper;
        private final String javaParser;
        private final String javaFormatter;

        Size(String javaName, String javaWrapper, String javaParser, String javaFormatter) {
            this.javaName = javaName;
            this.javaWrapper = javaWrapper;
            this.javaParser = javaParser;
            this.javaFormatter = javaFormatter;
        }
        public String getJavaName() {
            return this.javaName;
        }
        public String getJavaWrapper() {
            return this.javaWrapper;
        }
        public String getJavaParser() {
            return this.javaParser;
        }
        public String getJavaFormatter() {
            return this.javaFormatter;
        }
    }

    private Size size;

    protected FloatingPointType(Package pack, Size size) {
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
        if (t instanceof FloatingPointType) {
            return ((FloatingPointType) t).size == this.size;
        }
        return false;
    }

    public String getJavaName() {
        return this.size.getJavaName();
    }

    public String getJavaWrapper() {
        return this.size.getJavaWrapper();
    }
    
    public String javaConvertToString(String source) {
        return this.size.getJavaWrapper() + "." + this.size.getJavaFormatter() +
            "(" + source + ")";
    }
    
    public String javaConvertFromString(String source) {
        return this.size.getJavaWrapper() + "." + this.size.getJavaParser() +
            "(" + source + ")";
    }
}

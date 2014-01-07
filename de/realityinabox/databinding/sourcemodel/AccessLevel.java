package de.realityinabox.databinding.sourcemodel;

public enum AccessLevel {
    PRIVATE   ("private "),
    DEFAULT   (""),
    PROTECTED ("protected "),
    PUBLIC    ("public ");

    private final String javaName;
    
    AccessLevel(String javaName) {
        this.javaName = javaName;
    }

    public String getJavaName() {
        return this.javaName;
    }
}

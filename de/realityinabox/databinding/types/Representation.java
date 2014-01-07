package de.realityinabox.databinding.types;

public enum Representation {
    UNKNOWN, ATTRIBUTE, ELEMENT, CONTENT, DATA, VALUE;
    
    public String getRepresentationName(String content) {
        switch (this) {
            case ATTRIBUTE:
                return content + "-attribute";
            case ELEMENT:
                return content + "-element";
            case CONTENT:
                return content + "-value";
            case DATA:
                return content + "-value";
        }
        return content;
    }
    
    public static Representation getStronger(Representation a, Representation b) {
        if ((a == ATTRIBUTE) || (b == ATTRIBUTE)) return ATTRIBUTE;
        if ((a == ELEMENT) || (b == ELEMENT)) return ELEMENT;
        if ((a == VALUE) || (b == VALUE)) return VALUE;
        if ((a == DATA) || (b == DATA)) return DATA;
        if ((a == CONTENT) || (b == CONTENT)) return CONTENT;
        return UNKNOWN;
    }
}

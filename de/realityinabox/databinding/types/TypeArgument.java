package de.realityinabox.databinding.types;

public class TypeArgument {

    public enum Wildcard { NONE, EXTENDS, SUPER; }

    private ReferenceType type;
    private Wildcard wildcard;

    protected TypeArgument(ReferenceType type, Wildcard wildcard) {
        this.type = type;
        this.wildcard = wildcard;
    }

    protected TypeArgument(ReferenceType type) {
        this.type = type;
        this.wildcard = Wildcard.NONE;
    }

    public ReferenceType getType() {
        return type;
    }

    public Wildcard getWildcard() {
        return wildcard;
    }

    public String getJavaExpression() {
        if (this.wildcard == Wildcard.EXTENDS) return "? extends " + type.getJavaName();
        if (this.wildcard == Wildcard.SUPER)   return "? super "   + type.getJavaName();
        return type.getJavaName();
    }
    
    public boolean structurallyEquals(TypeArgument ta) {
        return type.structurallyEquals(ta.type) && (this.wildcard == ta.wildcard);
    }
}

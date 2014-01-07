package de.realityinabox.databinding.sourcemodel;

import de.realityinabox.databinding.types.Type;

public class Field extends Variable {
    
    private AccessLevel accessLevel = AccessLevel.DEFAULT;
    private String defaultExpression = null;

    protected Field(String name, Type type) {
        super(name, type);
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getDefaultExpression() {
        return defaultExpression;
    }

    public void setDefaultExpression(String defaultExpression) {
        this.defaultExpression = defaultExpression;
    }

    public void write(java.io.OutputStream stream, SourceContext context) throws java.io.IOException {
        String line = getAccessLevel().getJavaName();
        line += getType().getJavaName() + " " + getName();
        if ((defaultExpression != null) && (!defaultExpression.equals("")))
            line += " = " + defaultExpression;
        context.println(stream, line + ";");
    }
}

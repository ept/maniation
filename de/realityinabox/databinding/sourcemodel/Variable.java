package de.realityinabox.databinding.sourcemodel;

import de.realityinabox.databinding.types.Type;

public class Variable {

    private String name;
    private Type type;

    protected Variable(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}

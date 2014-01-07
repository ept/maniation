package de.realityinabox.databinding.sourcemodel;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import de.realityinabox.databinding.types.ReferenceType;
import de.realityinabox.databinding.types.Type;

public class Class extends ClassType {

    protected Class(Package pack, Type originatingType, String name) {
        super(pack, originatingType, name);
    }

    protected Class(Package pack, Type originatingType, ClassType enclosingClass, String name) {
        super(pack, originatingType, enclosingClass, name);
    }

    private ReferenceType superClass = null;
    private boolean staticModifier = false;
    private boolean finalModifier = false;
    private boolean strictfpModifier = false;
    private Map<String,Field> fields = new HashMap<String,Field>();
    private List<Constructor> constructors = new ArrayList<Constructor>();

    public ReferenceType getSuperClass() {
        return superClass;
    }

    public void setSuperClass(ReferenceType superClass) {
        this.superClass = superClass;
    }

    public boolean isAbstract() {
        for (Method meth : getMethods()) {
            if (meth.isAbstract()) return true;
        }
        return false;
    }

    public boolean isStatic() {
        return staticModifier;
    }

    public void setStatic(boolean value) {
        this.staticModifier = value;
    }

    public boolean isFinal() {
        return finalModifier;
    }

    public void setFinal(boolean value) {
        this.finalModifier = value;
    }

    public boolean isStrictfp() {
        return strictfpModifier;
    }

    public void setStrictfp(boolean value) {
        this.strictfpModifier = value;
    }

    public Collection<Field> getFields() {
        ArrayList<Field> result = new ArrayList<Field>();
        result.addAll(fields.values());
        return result;
    }

    public void addField(Field field) {
        assert(!fields.containsKey(field.getName()));
        fields.put(field.getName(), field);
    }
    
    public Collection<Constructor> getConstructors() {
        return constructors;
    }
    
    // Not public -- use SourceFactory.newConstructor()
    protected void addConstructor(Constructor c) {
        constructors.add(c);
    }
    
    public void write(java.io.OutputStream stream, SourceContext context) throws java.io.IOException {
        String line = getAccessLevel().getJavaName();
        if (isStatic())   line += "static ";
        if (isFinal())    line += "final ";
        if (isStrictfp()) line += "strictfp ";
        line += "class " + getName();
        if (getSuperClass() != null) line += " extends " + getSuperClass().getJavaName();
        String impl = "";
        for (ReferenceType t : getSuperInterfaces()) {
            if (!impl.equals("")) impl += ", ";
            impl += t.getJavaName();
        }
        if (!impl.equals("")) line += " implements " + impl;
        line += " {";
        context.println(stream, line);
        context.increaseIndent();
        context.println(stream, "");
        for (Field f : getFields()) f.write(stream, context.duplicate());
        for (Constructor c : getConstructors()) {
            context.println(stream, "");
            c.write(stream, context.duplicate());
        }
        for (Property p : getProperties()) {
            for (Field f : p.getFields()) f.write(stream, context.duplicate());
        }
        for (Property p : getProperties()) {
            for (Method m : p.getMethods()) {
                context.println(stream, "");
                m.write(stream, context.duplicate());
            }
        }
        for (Method m : getMethods()) {
            context.println(stream, "");
            m.write(stream, context.duplicate());
        }
        for (ClassType c : getNestedClasses()) {
            context.println(stream, ""); context.println(stream, "");
            c.write(stream, context.duplicate());
        }
        context.decreaseIndent();
        context.println(stream, "}");
    }
}

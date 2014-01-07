package de.realityinabox.databinding.sourcemodel;

import de.realityinabox.databinding.types.ReferenceType;
import de.realityinabox.databinding.types.Type;

public class Interface extends ClassType {
    
    protected Interface(Package pack, Type originatingType, String name) {
        super(pack, originatingType, name);
    }

    protected Interface(Package pack, Type originatingType,
            ClassType enclosingClass, String name) {
        super(pack, originatingType, enclosingClass, name);
    }

    public void write(java.io.OutputStream stream, SourceContext context) throws java.io.IOException {
        String line = getAccessLevel().getJavaName() + "interface " + getName();
        String ext = "";
        for (ReferenceType t : getSuperInterfaces()) {
            if (!ext.equals("")) ext += ", ";
            ext += t.getJavaName();
        }
        if (!ext.equals("")) line += " extends " + ext;
        line += " {";
        context.println(stream, line);
        context.increaseIndent();
        for (Property p : getProperties()) {
            for (Method m : p.getMethods()) context.println(stream, m.getSignature() + ";");
        }
        for (Method m : getMethods()) context.println(stream, m.getSignature() + ";");
        for (ClassType c : getNestedClasses()) {
            context.println(stream, ""); context.println(stream, "");
            c.write(stream, context.duplicate());
        }
        context.decreaseIndent();
        context.println(stream, "}");
    }
}

package de.realityinabox.databinding.sourcemodel;

import java.util.List;
import java.util.ArrayList;
import de.realityinabox.databinding.types.ReferenceType;
import de.realityinabox.databinding.types.Type;

public class Enum extends ClassType {

    private List<EnumConstant> constants = new ArrayList<EnumConstant>();
    
    protected Enum(Package pack, Type originatingType, String name) {
        super(pack, originatingType, name);
    }
    
    protected Enum(Package pack, Type originatingType, ClassType enclosingClass, String name) {
        super(pack, originatingType, enclosingClass, name);
    }

    public List<EnumConstant> getConstants() {
        ArrayList<EnumConstant> result = new ArrayList<EnumConstant>();
        result.addAll(constants);
        return result;
    }

    public void addConstant(EnumConstant constant) {
        constants.add(constant);
    }

    public void write(java.io.OutputStream stream, SourceContext context) throws java.io.IOException {
        String line = getAccessLevel().getJavaName() + "enum " + getName();
        String impl = "";
        for (ReferenceType t : getSuperInterfaces()) {
            if (!impl.equals("")) impl += ", ";
            impl += t.getJavaName();
        }
        if (!impl.equals("")) line += " implements " + impl;
        line += " {";
        context.println(stream, line);
        context.increaseIndent();
        line = "";
        for (EnumConstant c : getConstants()) {
            if (!line.equals("")) context.println(stream, line + ",");
            line = c.javaCode();
        }
        if (!line.equals("")) context.println(stream, line + ";");
        context.println(stream, "");
        for (Method m : getMethods()) context.println(stream, m.getSignature() + ";");
        for (ClassType c : getNestedClasses()) {
            context.println(stream, ""); context.println(stream, "");
            c.write(stream, context.duplicate());
        }
        context.decreaseIndent();
        context.println(stream, "}");
    }
}

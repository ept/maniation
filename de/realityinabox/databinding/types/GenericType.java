package de.realityinabox.databinding.types;

import java.util.List;
import java.util.ArrayList;
import de.realityinabox.databinding.sourcemodel.Package;
import de.realityinabox.databinding.sourcemodel.ClassType;

public class GenericType extends ReferenceType {
    
    private List<TypeArgument> arguments = new ArrayList<TypeArgument>();

    protected GenericType(Package pack, String className, String generatedName) {
        super(pack, className, generatedName);
    }

    protected GenericType(ClassType classType) {
        super(classType);
    }

    public List<TypeArgument> getTypeArguments() {
        return arguments;
    }

    public void addTypeArgument(TypeArgument argument) {
        arguments.add(argument);
    }
    
    public boolean structurallyEquals(Type t) {
        if (!super.structurallyEquals(t)) return false;
        if (t instanceof GenericType) {
            int i = 0;
            for (TypeArgument ta : ((GenericType) t).getTypeArguments()) {
                if (i >= arguments.size()) return false;
                if (!arguments.get(i).structurallyEquals(ta)) return false;
                i++;
            }
            return i == arguments.size() - 1;
        }
        return false;
    }

    public String getJavaName() {
        String result = "";
        for (TypeArgument arg : getTypeArguments()) {
            if (!result.equals("")) result += ", ";
            result += arg.getJavaExpression();
        }
        if (!result.equals("")) result = "<" + result + ">";
        return super.getJavaName() + result;
    }
}

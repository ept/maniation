package de.realityinabox.databinding.sourcemodel;

import java.util.List;
import java.util.ArrayList;

public class EnumConstant {

    private String name;
    private List<String> arguments = new ArrayList<String>();

    protected EnumConstant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public List<String> getArguments() {
        ArrayList<String> result = new ArrayList<String>();
        result.addAll(arguments);
        return result;
    }

    public void addArgument(String argumentExpr) {
        arguments.add(argumentExpr);
    }

    public String javaCode() {
        String result = "";
        for (String s : getArguments()) {
            if (!result.equals("")) result += ", ";
            result += s;
        }
        if (!result.equals("")) result = " (" + result + ")";
        return getName() + result;
    }
}

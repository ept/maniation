package de.realityinabox.databinding.sourcemodel;

import java.util.ArrayList;
import java.util.List;
import de.realityinabox.databinding.types.ReferenceType;

public class Constructor {

    private Class whichClass;
    private AccessLevel accessLevel = AccessLevel.DEFAULT;
    private List<Variable> arguments = new ArrayList<Variable>();
    private List<ReferenceType> exceptions = new ArrayList<ReferenceType>();
    private List<String> source = new ArrayList<String>();

    protected Constructor(Class whichClass) {
        this.whichClass = whichClass;
        whichClass.addConstructor(this);
    }
    
    public Class getWhichClass() {
        return whichClass;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public List<Variable> getArguments() {
        ArrayList<Variable> result = new ArrayList<Variable>();
        result.addAll(arguments);
        return result;
    }

    public void addArgument(Variable argument) {
        arguments.add(argument);
    }

    public List<ReferenceType> getExceptions() {
        ArrayList<ReferenceType> result = new ArrayList<ReferenceType>();
        result.addAll(exceptions);
        return result;
    }

    public void addException(ReferenceType exception) {
        exceptions.add(exception);
    }

    public void addSourceLine(String line) {
        source.add(line);
    }
    
    public String getSignature() {
        String args = "";
        for (Variable arg : getArguments()) {
            if (!args.equals("")) args += ", ";
            args += arg.getType().getJavaName() + " " + arg.getName();
        }
        String throw_exc = "";
        for (ReferenceType exc : getExceptions()) {
            if (!throw_exc.equals("")) throw_exc += ", ";
            throw_exc += exc.getJavaName();
        }
        if (!throw_exc.equals("")) throw_exc = " throws " + throw_exc;
        return getWhichClass().getName() + "(" + args + ")" + throw_exc;
    }

    public void write(java.io.OutputStream stream, SourceContext context) throws java.io.IOException {
        String line = accessLevel.getJavaName();
        line += getSignature();
        context.println(stream, line + " {");
        context.increaseIndent();
        try {
            for (String l : source) context.println(stream, l);
        } finally {
            context.decreaseIndent();
        }
        context.println(stream, "}");
    }
}

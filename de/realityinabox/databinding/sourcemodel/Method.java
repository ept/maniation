package de.realityinabox.databinding.sourcemodel;

import java.util.List;
import java.util.ArrayList;
import de.realityinabox.databinding.types.Type;
import de.realityinabox.databinding.types.ReferenceType;

public class Method {

    private String name;
    private AccessLevel accessLevel = AccessLevel.DEFAULT;
    private boolean abstractModifier = false;
    private boolean staticModifier = false;
    private boolean finalModifier = false;
    private boolean synchronizedModifier = false;
    private boolean nativeModifier = false;
    private boolean strictfpModifier = false;
    private Type returnType;
    private List<Variable> arguments = new ArrayList<Variable>();
    private List<ReferenceType> exceptions = new ArrayList<ReferenceType>();
    private List<String> source = new ArrayList<String>();

    protected Method(String name, Type returnType) {
        this.name = name;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public boolean isAbstract() {
        return abstractModifier;
    }
    
    public void setAbstract(boolean value) {
        this.abstractModifier = value;
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
        
    public boolean isSynchronized() {
        return synchronizedModifier;
    }
    
    public void setSynchronized(boolean value) {
        this.synchronizedModifier = value;
    }
    
    public boolean isNative() {
        return nativeModifier;
    }
    
    public void setNative(boolean value) {
        this.nativeModifier = value;
    }
    
    public boolean isStrictfp() {
        return strictfpModifier;
    }
    
    public void setStrictfp(boolean value) {
        this.strictfpModifier = value;
    }
    
    public Type getReturnType() {
        return returnType;
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
        return getReturnType().getJavaName() + " " + getName() + "(" + args + ")" + throw_exc;
    }

    public void write(java.io.OutputStream stream, SourceContext context) throws java.io.IOException {
        String line = accessLevel.getJavaName();
        if (isAbstract())     line += "abstract ";
        if (isStatic())       line += "static ";
        if (isFinal())        line += "final ";
        if (isSynchronized()) line += "synchronized ";
        if (isNative())       line += "native ";
        if (isStrictfp())     line += "strictfp ";
        line += getSignature();
        if (isAbstract() || isNative()) {
            context.println(stream, line + ";");
        } else {
            context.println(stream, line + " {");
            context.increaseIndent();
            for (String l : source) context.println(stream, l);
            context.decreaseIndent();
            context.println(stream, "}");
        }
    }
}

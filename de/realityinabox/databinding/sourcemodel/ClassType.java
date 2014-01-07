package de.realityinabox.databinding.sourcemodel;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import de.realityinabox.databinding.types.ReferenceType;
import de.realityinabox.databinding.types.Type;

public abstract class ClassType {

    private Package pack;
    private Type originatingType;
    private ClassType enclosingClass;
    private String name;
    private AccessLevel accessLevel = AccessLevel.DEFAULT;
    private Collection<ReferenceType> superInterfaces = new ArrayList<ReferenceType>();
    private Map<String,ClassType> nestedClasses = new HashMap<String,ClassType>();
    private Collection<Method> methods = new ArrayList<Method>();
    private Collection<Property> properties = new ArrayList<Property>();

    protected ClassType(Package pack, Type originatingType, String name) {
        this.pack = pack;
        this.originatingType = originatingType;
        this.enclosingClass = null;
        this.name = name;
    }
    
    protected ClassType(Package pack, Type originatingType, ClassType enclosingClass,
            String name) {
        this.pack = pack;
        this.originatingType = originatingType;
        this.enclosingClass = enclosingClass;
        this.name = name;
    }
    
    public Package getPackage() {
        return pack;
    }
    
    public Type getOriginatingType() {
        return originatingType;
    }
    
    public ClassType getEnclosingClass() {
        return enclosingClass;
    }
    
    public String getName() {
        return name;
    }

    public String getFullName() {
        if (enclosingClass != null) 
            return enclosingClass.getPackage().getName() + "." +
                enclosingClass.getName() + "." + getName();
        return getPackage().getName() + "." + getName();
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public Collection<ReferenceType> getSuperInterfaces() {
        ArrayList<ReferenceType> result = new ArrayList<ReferenceType>();
        result.addAll(superInterfaces);
        return result;
    }

    public void addSuperInterface(ReferenceType superInterface) {
        superInterfaces.add(superInterface);
    }

    public Collection<ClassType> getNestedClasses() {
        ArrayList<ClassType> result = new ArrayList<ClassType>();
        result.addAll(nestedClasses.values());
        return result;
    }

    public void addNestedClass(ClassType cls) {
        assert(!nestedClasses.containsKey(cls.getName()));
        nestedClasses.put(cls.getName(), cls);
    }
    
    public Collection<Method> getMethods() {
        ArrayList<Method> result = new ArrayList<Method>();
        result.addAll(methods);
        return result;
    }
    
    public void addMethod(Method method) {
        methods.add(method);
    }

    public Collection<Property> getProperties() {
        ArrayList<Property> result = new ArrayList<Property>();
        result.addAll(properties);
        return result;
    }

    public void addProperty(Property property) {
        properties.add(property);
    }

    public abstract void write(java.io.OutputStream stream, SourceContext context) throws java.io.IOException;
}

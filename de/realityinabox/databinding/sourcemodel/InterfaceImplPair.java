package de.realityinabox.databinding.sourcemodel;

import java.util.Collection;
import java.util.ArrayList;
import de.realityinabox.databinding.types.ReferenceType;
import de.realityinabox.databinding.types.Type;
import de.realityinabox.databinding.types.TypeFactory;

public class InterfaceImplPair extends Class {

    private Interface ifc;
    private boolean writingXML = false;

    protected InterfaceImplPair(Package pack, Type originatingType, String name) {
        super(pack, originatingType, name + "Impl");
        ifc = SourceFactory.newInterface(pack, originatingType, name);
        ifc.setAccessLevel(AccessLevel.PUBLIC);
        pack.addClass(ifc);
        super.addSuperInterface(TypeFactory.newReferenceType(ifc));
        super.setAccessLevel(AccessLevel.DEFAULT);
    }

    public String getFullName() {
        return getPackage().getName() + "." + ifc.getName();
    }

    public AccessLevel getAccessLevel() {
        if (writingXML) return super.getAccessLevel();
        return ifc.getAccessLevel();
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        ifc.setAccessLevel(accessLevel);
    }

    public AccessLevel getImplAccessLevel() {
        return super.getAccessLevel();
    }

    public void setImplAccessLevel(AccessLevel accessLevel) {
        super.setAccessLevel(accessLevel);
    }

    public Collection<ReferenceType> getSuperInterfaces() {
        if (writingXML) return super.getSuperInterfaces();
        return ifc.getSuperInterfaces();
    }

    public void addSuperInterface(ReferenceType superInterface) {
        ifc.addSuperInterface(superInterface);
    }
    
    public Collection<ReferenceType> getImplSuperInterfaces() {
        return super.getSuperInterfaces();
    }
    
    public void addImplSuperInterface(ReferenceType superInterface) {
        super.addSuperInterface(superInterface);
    }

    public Collection<Method> getMethods() {
        if (writingXML) {
            ArrayList<Method> result = new ArrayList<Method>();
            result.addAll(ifc.getMethods());
            result.addAll(super.getMethods());
            return result;
        } 
        return ifc.getMethods();
    }

    public void addMethod(Method method) {
        ifc.addMethod(method);
    }

    public Collection<Method> getImplMethods() {
        return super.getMethods();
    }

    public void addImplMethod(Method method) {
        super.addMethod(method);
    }

    public Collection<Property> getProperties() {
        return ifc.getProperties();
    }

    public void addProperty(Property property) {
        ifc.addProperty(property);
    }

    public void addNestedClass(ClassType cls) {
        ifc.addNestedClass(cls);
    }

    public Collection<ClassType> getNestedClasses() {
        if (writingXML) return super.getNestedClasses();
        return ifc.getNestedClasses();
    }
    
    public void addImplNestedClass(ClassType cls) {
        super.addNestedClass(cls);
    }

    public Collection<ClassType> getImplNestedClasses() {
        return super.getNestedClasses();
    }
    
    public void write(java.io.OutputStream stream, SourceContext context) throws java.io.IOException {
        writingXML = true;
        try {
            super.write(stream, context);
        } finally {
            writingXML = false;
        }
    }
}

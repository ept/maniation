package de.realityinabox.databinding.sourcemodel;

import de.realityinabox.databinding.types.Type;

public class SourceFactory {

    private static SourceFactory factory = new SourceFactory();
    
    protected SourceFactory() {}
    
    public static void setFactory(SourceFactory factory) {
        SourceFactory.factory = factory;
    }

    public static Class newClass(Package pack, Type originatingType, String name) {
        return factory._newClass(pack, originatingType, name);
    }
    
    protected Class _newClass(Package pack, Type originatingType, String name) {
        return new Class(pack, originatingType, name);
    }
    
    public static Class newClass(Package pack, Type originatingType, ClassType enclosingClass, String name) {
        return factory._newClass(pack, originatingType, enclosingClass, name);
    }
    
    protected Class _newClass(Package pack, Type originatingType, ClassType enclosingClass, String name) {
        return new Class(pack, originatingType, enclosingClass, name);
    }
    
    public static Constructor newConstructor(Class whichClass) {
        return factory._newConstructor(whichClass);
    }
    
    protected Constructor _newConstructor(Class whichClass) {
        return new Constructor(whichClass);
    }
    
    public static Enum newEnum(Package pack, Type originatingType, String name) {
        return factory._newEnum(pack, originatingType, name);
    }
    
    protected Enum _newEnum(Package pack, Type originatingType, String name) {
        return new Enum(pack, originatingType, name);
    }
    
    public static Enum newEnum(Package pack, Type originatingType,
            ClassType enclosingClass, String name) {
        return factory._newEnum(pack, originatingType, enclosingClass, name);
    }
    
    protected Enum _newEnum(Package pack, Type originatingType, 
            ClassType enclosingClass, String name) {
        return new Enum(pack, originatingType, enclosingClass, name);
    }
    
    public static EnumConstant newEnumConstant(String name) {
        return factory._newEnumConstant(name);
    }
    
    protected EnumConstant _newEnumConstant(String name) {
        return new EnumConstant(name);
    }
    
    public static Field newField(String name, Type type) {
        return factory._newField(name, type);
    }
    
    protected Field _newField(String name, Type type) {
        return new Field(name, type);
    }

    public static Interface newInterface(Package pack, Type originatingType, String name) {
        return factory._newInterface(pack, originatingType, name);
    }
    
    protected Interface _newInterface(Package pack, Type originatingType, String name) {
        return new Interface(pack, originatingType, name);
    }
    
    public static Interface newInterface(Package pack, Type originatingType,
            ClassType enclosingClass, String name) {
        return factory._newInterface(pack, originatingType, enclosingClass, name);
    }
    
    protected Interface _newInterface(Package pack, Type originatingType, 
            ClassType enclosingClass, String name) {
        return new Interface(pack, originatingType, enclosingClass, name);
    }
    
    public static InterfaceImplPair newInterfaceImplPair(Package pack,
            Type originatingType, String name) {
        return factory._newInterfaceImplPair(pack, originatingType, name);
    }
    
    protected InterfaceImplPair _newInterfaceImplPair(Package pack,
            Type originatingType, String name) {
        return new InterfaceImplPair(pack, originatingType, name);
    }
    
    public static Method newMethod(String name, Type returnType) {
        return factory._newMethod(name, returnType);
    }
    
    protected Method _newMethod(String name, Type type) {
        return new Method(name, type);
    }

    public static MultiProperty newMultiProperty(Package pack, String name, Type type) {
        return factory._newMultiProperty(pack, name, type);
    }
    
    protected MultiProperty _newMultiProperty(Package pack, String name, Type type) {
        return new MultiProperty(pack, name, type);
    }
    
    public static Package newPackage(String name) {
        return factory._newPackage(name);
    }
    
    protected Package _newPackage(String name) {
        return new Package(name);
    }
    
    public static Property newProperty(Package pack, String name, Type type) {
        return factory._newProperty(pack, name, type);
    }
    
    protected Property _newProperty(Package pack, String name, Type type) {
        return new Property(pack, name, type);
    }
    
    public static Variable newVariable(String name, Type type) {
        return factory._newVariable(name, type);
    }
    
    protected Variable _newVariable(String name, Type type) {
        return new Variable(name, type);
    }
}

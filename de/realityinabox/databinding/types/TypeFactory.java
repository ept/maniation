package de.realityinabox.databinding.types;

import de.realityinabox.databinding.sourcemodel.ClassType;
import de.realityinabox.databinding.sourcemodel.Package;
import de.realityinabox.databinding.types.TypeArgument.Wildcard;

public class TypeFactory {
    
    private static TypeFactory factory = new TypeFactory();

    protected TypeFactory() {}

    public static void setFactory(TypeFactory factory) {
        TypeFactory.factory = factory;
    }
    
    public static AlternativesType newAlternativesType(Package pack, String name) {
        return factory._newAlternativesType(pack, name);
    }
    
    protected AlternativesType _newAlternativesType(Package pack, String name) {
        return new AlternativesType(pack, name);
    }
    
    public static ArrayType newArrayType(Type elementType) {
        return factory._newArrayType(elementType);
    }
    
    protected ArrayType _newArrayType(Type elementType) {
        return new ArrayType(elementType);
    }
    
    public static BooleanType newBooleanType(Package pack) {
        return factory._newBooleanType(pack);
    }
    
    protected BooleanType _newBooleanType(Package pack) {
        return new BooleanType(pack);
    }

    public static FloatingPointType newFloatingPointType(Package pack, FloatingPointType.Size size) {
        return factory._newFloatingPointType(pack, size);
    }
    
    protected FloatingPointType _newFloatingPointType(Package pack, FloatingPointType.Size size) {
        return new FloatingPointType(pack, size);
    }
    
    public static GenericType newGenericType(Package pack, String className, String generatedName) {
        return factory._newGenericType(pack, className, generatedName);
    }
    
    protected GenericType _newGenericType(Package pack, String className, String generatedName) {
        return new GenericType(pack, className, generatedName);
    }

    public static GenericType newGenericType(ClassType classType) {
        return factory._newGenericType(classType);
    }
    
    protected GenericType _newGenericType(ClassType classType) {
        return new GenericType(classType);
    }
    
    public static GroupType newGroupType(Package pack, String name) {
        return factory._newGroupType(pack, name);
    }
    
    protected GroupType _newGroupType(Package pack, String name) {
        return new GroupType(pack, name);
    }

    public static IntegralType newIntegralType(Package pack, IntegralType.Size size) {
        return factory._newIntegralType(pack, size);
    }
    
    protected IntegralType _newIntegralType(Package pack, IntegralType.Size size) {
        return new IntegralType(pack, size);
    }
    
    public static KeyRefType newKeyRefType(Type actualType, String keyName) {
        return factory._newKeyRefType(actualType, keyName);
    }
    
    protected KeyRefType _newKeyRefType(Type actualType, String keyName) {
        return new KeyRefType(actualType, keyName);
    }
    
    public static KeyType newKeyType(Type actualType, String keyName) {
        return factory._newKeyType(actualType, keyName);
    }
    
    protected KeyType _newKeyType(Type actualType, String keyName) {
        return new KeyType(actualType, keyName);
    }

    public static ListType newListType(Package pack, String name) {
        return factory._newListType(pack, name);
    }
    
    protected ListType _newListType(Package pack, String name) {
        return new ListType(pack, name);
    }

    public static ReferenceType newReferenceType(Package pack, String className, String generatedName) {
        return factory._newReferenceType(pack, className, generatedName);
    }
    
    protected ReferenceType _newReferenceType(Package pack, String className, String generatedName) {
        return new ReferenceType(pack, className, generatedName);
    }

    public static ReferenceType newReferenceType(ClassType classType) {
        return factory._newReferenceType(classType);
    }
    
    protected ReferenceType _newReferenceType(ClassType classType) {
        return new ReferenceType(classType);
    }
    
    public static TypeArgument newTypeArgument(ReferenceType type, Wildcard wildcard) {
        return factory._newTypeArgument(type, wildcard);
    }
    
    protected TypeArgument _newTypeArgument(ReferenceType type, Wildcard wildcard) {
        return new TypeArgument(type, wildcard);
    }

    public static TypeArgument newTypeArgument(ReferenceType type) {
        return factory._newTypeArgument(type);
    }
    
    protected TypeArgument _newTypeArgument(ReferenceType type) {
        return new TypeArgument(type);
    }

    public static VoidType newVoidType(Package pack) {
        return factory._newVoidType(pack);
    }
    
    protected VoidType _newVoidType(Package pack) {
        return new VoidType(pack);
    }
}

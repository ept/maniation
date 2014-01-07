package de.realityinabox.databinding.xml;

import java.util.List;

import javax.xml.namespace.QName;

import de.realityinabox.databinding.sourcemodel.AccessLevel;
import de.realityinabox.databinding.sourcemodel.Class;
import de.realityinabox.databinding.sourcemodel.Constructor;
import de.realityinabox.databinding.sourcemodel.Field;
import de.realityinabox.databinding.sourcemodel.InterfaceImplPair;
import de.realityinabox.databinding.sourcemodel.Method;
import de.realityinabox.databinding.sourcemodel.Property;
import de.realityinabox.databinding.sourcemodel.SourceFactory;
import de.realityinabox.databinding.sourcemodel.Tools;
import de.realityinabox.databinding.types.IntegralType;
import de.realityinabox.databinding.types.Type;
import de.realityinabox.databinding.types.TypeFactory;

public class MyAttributesClass extends Class {
    
    private XMLPackage pack;
    private InterfaceImplPair enclosingClass;
    private boolean hasSuperClass;
    private List<Attr> attributes;

    MyAttributesClass(XMLPackage pack, InterfaceImplPair enclosingClass, boolean hasSuperClass) {
        super(pack, null, enclosingClass, "MyAttributes");
        this.pack = pack;
        this.enclosingClass = enclosingClass;
        this.hasSuperClass = hasSuperClass;
        this.attributes = new java.util.ArrayList<Attr>();
        setSuperClass(TypeFactory.newReferenceType(pack, 
                "de.realityinabox.databinding.libs.AttributeSet", "AttributeSet"));
        setAccessLevel(AccessLevel.PRIVATE);
        determineAttributes();
        addConstructor();
        addSizeMethod();
        addAddMethod();
        addGetKeyMethod();
        addGetValueMethod();
        addRemoveMethod();
    }
    
    private void determineAttributes() {
        for (Property p : enclosingClass.getProperties()) {
            Object o = p.getType().getExtendedInfo().get("xml");
            if ((o != null) && (o instanceof ExtendedInfo)) {
                ExtendedInfo ei = (ExtendedInfo) o;
                if (ei.getRepresentation() == ExtendedInfo.Representation.ATTRIBUTE)
                    attributes.add(new Attr(p, ei.getName()));
            }
        }
    }
    
    private void addConstructor() {
        Constructor c = SourceFactory.newConstructor(this);
        if (!hasSuperClass) c.addSourceLine("super(null);"); else
        c.addSourceLine("super(" + enclosingClass.getName() + ".super.getAttributes().entrySet());");
    }
    
    private void addSizeMethod() {
        Method m = SourceFactory.newMethod("size", 
                TypeFactory.newIntegralType(pack, IntegralType.Size.INT));
        m.setAccessLevel(AccessLevel.PUBLIC);
        m.addSourceLine("return " + (hasSuperClass ? "super.size() + " : "") +
                attributes.size() + ";");
        addMethod(m);
    }
    
    private void addAddMethod() {
        Method m = SourceFactory.newMethod("add", TypeFactory.newReferenceType(pack, 
                "java.lang.String", "String"));
        m.setAccessLevel(AccessLevel.PUBLIC);
        Type t = TypeFactory.newReferenceType(pack, "javax.xml.namespace.QName", "QName");
        m.addArgument(SourceFactory.newVariable("key", t));
        t = TypeFactory.newReferenceType(pack, "java.lang.String", "String");
        m.addArgument(SourceFactory.newVariable("value", t));
        if (attributes.size() > 0) m.addSourceLine("java.lang.String _result = null;");
        for (Attr att : attributes) {
            m.addSourceLine("if (key.equals(" + att.qNameFieldName + ")) {");
            if (att.getter != null) m.addSourceLine("    _result = " +
                    att.generateAccessorCode() + ";");
            if (att.setter != null) m.addSourceLine("    " + att.generateMutatorCode("value"));
            m.addSourceLine("} else");
        }
        if (hasSuperClass) m.addSourceLine("_result = super.add(key, value);");
        else m.addSourceLine("throw new java.lang.IllegalArgumentException(" +
                "\"XML attribute '\" + key.getLocalPart() + \"' is unknown\");");
        if (attributes.size() > 0) m.addSourceLine("return _result;");
        addMethod(m);
    }
    
    private void addGetKeyMethod() {
        Method m = SourceFactory.newMethod("getKey", TypeFactory.newReferenceType(pack, 
                "javax.xml.namespace.QName", "QName"));
        m.setAccessLevel(AccessLevel.PUBLIC);
        Type t = TypeFactory.newIntegralType(pack, IntegralType.Size.INT);
        m.addArgument(SourceFactory.newVariable("index", t));
        for (int i=0; i<attributes.size(); i++) {
            m.addSourceLine("if (index == " + i + ") return " +
                    attributes.get(i).qNameFieldName + ";");
        }
        if (hasSuperClass) m.addSourceLine("return super.getKey(index - " + attributes.size() + ");");
        else m.addSourceLine("throw new IllegalArgumentException();");
        addMethod(m);
    }
    
    private void addGetValueMethod() {
        Method m = SourceFactory.newMethod("getValue", TypeFactory.newReferenceType(pack, 
                "java.lang.String", "String"));
        m.setAccessLevel(AccessLevel.PUBLIC);
        Type t = TypeFactory.newIntegralType(pack, IntegralType.Size.INT);
        m.addArgument(SourceFactory.newVariable("index", t));
        for (int i=0; i<attributes.size(); i++) {
            if (attributes.get(i).getter != null) m.addSourceLine("if (index == " + i + ") return " +
                    attributes.get(i).generateAccessorCode() + ";");
        }
        if (hasSuperClass) m.addSourceLine("return super.getValue(index - " + attributes.size() + ");");
        else m.addSourceLine("throw new IllegalArgumentException();");
        addMethod(m);        
    }
    
    private void addRemoveMethod() {
        Method m = SourceFactory.newMethod("remove", TypeFactory.newVoidType(pack));
        m.setAccessLevel(AccessLevel.PUBLIC);
        Type t = TypeFactory.newIntegralType(pack, IntegralType.Size.INT);
        m.addArgument(SourceFactory.newVariable("index", t));
        m.addSourceLine("if ((index >= 0) && (index < " + attributes.size() + ")) return;");
        if (hasSuperClass) m.addSourceLine("super.remove(index - " + attributes.size() + ");");
        else m.addSourceLine("throw new IllegalArgumentException();");
        addMethod(m);        
    }

    
    private class Attr {
        String qNameFieldName;
        Property property;
        Method getter, setter;
        
        Attr(Property p, QName name) {
            this.property = p;
            qNameFieldName = "_" + Tools.toJavaName(p.getName(), false) + "Attribute";
            Field f = SourceFactory.newField(qNameFieldName, TypeFactory.newReferenceType(pack,
                    "javax.xml.namespace.QName", "QName"));
            f.setDefaultExpression("new javax.xml.namespace.QName(\"" +
                    name.getNamespaceURI() + "\", \"" +
                    name.getLocalPart() + "\")");
            addField(f);
            getter = null; setter = null;
            Type voidType = TypeFactory.newVoidType(pack);
            for (Method meth : p.getMethods()) {
                if (meth.getReturnType().structurallyEquals(p.getType()) &&
                        (meth.getArguments().size() == 0))
                    getter = meth;
                if (meth.getReturnType().structurallyEquals(voidType) &&
                        (meth.getArguments().size() == 1) &&
                        (meth.getArguments().get(0).getType().structurallyEquals(p.getType())))
                    setter = meth;
            }
        }
        
        String generateAccessorCode() {
            return property.getType().javaConvertToString(getter.getName() + "()");            
        }
        
        String generateMutatorCode(String source) {
            if (property instanceof XMLProperty) {
                String code = ((XMLProperty) property).generateMutatorCode(source);
                if (code != null) return code;
            }
            return setter.getName() + "(" + property.getType().javaConvertFromString(source) + ");";
        }
    }
}

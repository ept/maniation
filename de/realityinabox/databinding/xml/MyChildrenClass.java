package de.realityinabox.databinding.xml;

//import java.util.List;
import de.realityinabox.databinding.sourcemodel.AccessLevel;
import de.realityinabox.databinding.sourcemodel.Class;
import de.realityinabox.databinding.sourcemodel.InterfaceImplPair;
import de.realityinabox.databinding.sourcemodel.Method;
//import de.realityinabox.databinding.sourcemodel.Property;
import de.realityinabox.databinding.sourcemodel.SourceFactory;
import de.realityinabox.databinding.types.GenericType;
import de.realityinabox.databinding.types.IntegralType;
import de.realityinabox.databinding.types.Type;
import de.realityinabox.databinding.types.TypeFactory;

public class MyChildrenClass extends Class {

    private XMLPackage pack;
    private InterfaceImplPair enclosingClass;
    private boolean hasSuperClass;
    //private List<Property> children;

    MyChildrenClass(XMLPackage pack, InterfaceImplPair enclosingClass, boolean hasSuperClass) {
        super(pack, null, enclosingClass, "MyChildren");
        this.pack = pack;
        this.enclosingClass = enclosingClass;
        this.hasSuperClass = hasSuperClass;
        //this.children = new java.util.ArrayList<Property>();
        GenericType gt = TypeFactory.newGenericType(pack, "java.util.AbstractList", "AbstractList");
        gt.addTypeArgument(TypeFactory.newTypeArgument(TypeFactory.newReferenceType(pack,
                "de.realityinabox.databinding.libs.XMLChild", "XMLChild")));
        setSuperClass(gt);
        setAccessLevel(AccessLevel.PRIVATE);
        addOwnSizeMethod();
        addGetMethod();
        addSizeMethod();
        addSetMethod();
        addAddMethod();
        addRemoveMethod();
    }
    
    private void addOwnSizeMethod() {
        Method m = SourceFactory.newMethod("ownSize", 
                TypeFactory.newIntegralType(pack, IntegralType.Size.INT));
        m.setAccessLevel(AccessLevel.PRIVATE);
        m.addSourceLine("int _i = 0;");
        m.addSourceLine("return _i;");
        addMethod(m);
    }
    
    private void addGetMethod() {
        Method m = SourceFactory.newMethod("get", TypeFactory.newReferenceType(pack,
                "de.realityinabox.databinding.libs.XMLElement", "XMLElement"));
        m.setAccessLevel(AccessLevel.PUBLIC);
        Type t = TypeFactory.newIntegralType(pack, IntegralType.Size.INT);
        m.addArgument(SourceFactory.newVariable("index", t));
        if (hasSuperClass) m.addSourceLine("return " + enclosingClass.getName() +
                ".super.getChildren().get(index);");
        else m.addSourceLine("throw new IllegalArgumentException();");
        addMethod(m);
    }
    
    private void addSizeMethod() {
        Method m = SourceFactory.newMethod("size", 
                TypeFactory.newIntegralType(pack, IntegralType.Size.INT));
        m.setAccessLevel(AccessLevel.PUBLIC);
        m.addSourceLine("return ownSize()" + (hasSuperClass ? " + " +
                enclosingClass.getName() + ".super.getChildren().size()" : "") + ";");
        addMethod(m);
    }
    
    private void addSetMethod() {
        Method m = SourceFactory.newMethod("set", TypeFactory.newReferenceType(pack,
                "de.realityinabox.databinding.libs.XMLElement", "XMLElement"));
        m.setAccessLevel(AccessLevel.PUBLIC);
        Type t = TypeFactory.newIntegralType(pack, IntegralType.Size.INT);
        m.addArgument(SourceFactory.newVariable("index", t));
        t = TypeFactory.newReferenceType(pack,
                "de.realityinabox.databinding.libs.XMLElement", "XMLElement");
        m.addArgument(SourceFactory.newVariable("element", t));
        m.addSourceLine(pack.getChildInterface().getFullName() + " _result;");
        m.addSourceLine("try {");
        if (hasSuperClass) m.addSourceLine("    return " + enclosingClass.getName() +
                ".super.getChildren().set(index, element);");
        else m.addSourceLine("    throw new java.lang.IllegalArgumentException();");
        m.addSourceLine("} catch (java.lang.ClassCastException e) {");
        m.addSourceLine("    throw new java.lang.IllegalArgumentException(e);");
        m.addSourceLine("}");
        addMethod(m);
    }
    
    private void addAddMethod() {
        Method m = SourceFactory.newMethod("add", TypeFactory.newVoidType(pack));
        m.setAccessLevel(AccessLevel.PUBLIC);
        Type t = TypeFactory.newIntegralType(pack, IntegralType.Size.INT);
        m.addArgument(SourceFactory.newVariable("index", t));
        t = TypeFactory.newReferenceType(pack,
                "de.realityinabox.databinding.libs.XMLElement", "XMLElement");
        m.addArgument(SourceFactory.newVariable("element", t));
        if (hasSuperClass) m.addSourceLine(enclosingClass.getName() +
            ".super.getChildren().add(index - ownSize(), element);");
        else {
            m.addSourceLine("if (element instanceof " + pack.getElementInterface().getFullName() + ")");
            m.addSourceLine("    throw new java.lang.IllegalArgumentException(" +
                    "\"XML element '\" + ");
            m.addSourceLine("        ((" + pack.getElementInterface().getFullName() + 
                    ") element).getTagName().getLocalPart() + \"' is unknown\");");
        }
        addMethod(m);
    }
    
    private void addRemoveMethod() {
        Method m = SourceFactory.newMethod("remove", TypeFactory.newReferenceType(pack,
                "de.realityinabox.databinding.libs.XMLElement", "XMLElement"));
        m.setAccessLevel(AccessLevel.PUBLIC);
        Type t = TypeFactory.newIntegralType(pack, IntegralType.Size.INT);
        m.addArgument(SourceFactory.newVariable("index", t));
        if (hasSuperClass) m.addSourceLine("return " + enclosingClass.getName() +
            ".super.getChildren().remove(index);");
        else m.addSourceLine("throw new java.lang.IllegalArgumentException();");
        addMethod(m);
    }

/*
        private int ownSize() {
            if (nameClass == null) return 0; else return 1;
        }
        
        public XMLChild get(int index) {
            if (nameClass != null) {
                if (index == 0) return nameClass;
                index--;
            }
            return ElementTag.super.getChildren().get(index);
        }
        
        public int size() {
            return ElementTag.super.getChildren().size() + ownSize();
        }
        
        public XMLChild set(int index, XMLChild element) {
            XMLChild result;
            try {
                if (nameClass != null) {
                    if (index == 0) {
                        result = nameClass;
                        nameClass = (NameClass) element;
                        return result;
                    }
                    index--;
                }
                return ElementTag.super.getChildren().set(index, element);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException(e);
            }
        }
        
        public void add(int index, XMLChild element) {
            if (element == null) return;
            try {
                nameClass = (NameClass) element;
                return;
            } catch (ClassCastException e) {}
            ElementTag.super.getChildren().add(index - ownSize(), element);
        }

        public XMLChild remove(int index) {
            XMLChild result;
            if (nameClass != null) {
                if (index == 0) {
                    result = nameClass;
                    nameClass = null;
                    return result;
                }
                index--;
            }
            return ElementTag.super.getChildren().remove(index);
        }
 */
}

package de.realityinabox.databinding.xml;

import java.util.Collection;
import de.realityinabox.databinding.sourcemodel.AccessLevel;
import de.realityinabox.databinding.sourcemodel.Class;
import de.realityinabox.databinding.sourcemodel.ClassType;
import de.realityinabox.databinding.sourcemodel.Constructor;
import de.realityinabox.databinding.sourcemodel.Field;
import de.realityinabox.databinding.sourcemodel.Interface;
import de.realityinabox.databinding.sourcemodel.InterfaceImplPair;
import de.realityinabox.databinding.sourcemodel.Method;
import de.realityinabox.databinding.sourcemodel.SourceFactory;
import de.realityinabox.databinding.types.GenericType;
import de.realityinabox.databinding.types.ReferenceType;
import de.realityinabox.databinding.types.Type;
import de.realityinabox.databinding.types.TypeArgument;
import de.realityinabox.databinding.types.TypeFactory;

public class XMLPackage extends de.realityinabox.databinding.sourcemodel.Package {
    
    private DocumentClass document;
    private ParserClass parser = null;
    private Interface childInterface;
    private Interface elementInterface;

    protected XMLPackage(String name) {
        super(name);
        childInterface = SourceFactory.newInterface(this, null, "XMLChild");
        childInterface.addSuperInterface(TypeFactory.newReferenceType(this,
                "de.realityinabox.databinding.libs.XMLChild", "XMLChild"));
        childInterface.addMethod(newGetDocumentMethod());
        addClass(childInterface);
        elementInterface = SourceFactory.newInterface(this, null, "XMLElement");
        elementInterface.addSuperInterface(TypeFactory.newReferenceType(childInterface));
        elementInterface.addSuperInterface(TypeFactory.newReferenceType(this,
                "de.realityinabox.databinding.libs.XMLElement", "XMLElement"));
        elementInterface.addMethod(newGetParentMethod());
        elementInterface.addMethod(newGetTagNameMethod());
        elementInterface.addMethod(newSetTagNameMethod());
        elementInterface.addMethod(newGetChildrenMethod());
        elementInterface.addMethod(newGetAttributesMethod());
        elementInterface.addMethod(newGetParseHandlerMethod());
        addClass(elementInterface);
        document = new DocumentClass(this);
        addClass(document);
    }
    
    Method newGetDocumentMethod() {
        Method m = SourceFactory.newMethod("getDocument", TypeFactory.newReferenceType(this,
                "de.realityinabox.databinding.libs.Document", "Document"));
        m.setAccessLevel(AccessLevel.PUBLIC);
        m.addSourceLine("return this._document;");
        return m;
    }

    Method newGetParentMethod() {
        Method m = SourceFactory.newMethod("getParent", 
                TypeFactory.newReferenceType(elementInterface));
        m.setAccessLevel(AccessLevel.PUBLIC);
        m.addSourceLine("return this._parent;");
        return m;
    }
    
    Method newGetTagNameMethod() {
        Type t = TypeFactory.newReferenceType(this, "javax.xml.namespace.QName", "QName");
        Method m = SourceFactory.newMethod("getTagName", t);
        m.setAccessLevel(AccessLevel.PUBLIC);
        m.addSourceLine("return this._tagName;");
        return m;
    }
    
    Method newSetTagNameMethod() {
        Method m = SourceFactory.newMethod("setTagName", TypeFactory.newVoidType(this));
        Type t = TypeFactory.newReferenceType(this, "javax.xml.namespace.QName", "QName");
        m.addArgument(SourceFactory.newVariable("tagName", t));
        m.setAccessLevel(AccessLevel.PUBLIC);
        m.addSourceLine("this._tagName = tagName;");
        return m;
    }
    
    Method newGetChildrenMethod() {
        GenericType gt = TypeFactory.newGenericType(this, "java.util.List", "List");
        TypeArgument ta = TypeFactory.newTypeArgument(TypeFactory.newReferenceType(this,
                "de.realityinabox.databinding.libs.XMLChild", "XMLChild"));
        gt.addTypeArgument(ta);
        Method m = SourceFactory.newMethod("getChildren", gt);
        m.setAccessLevel(AccessLevel.PUBLIC);
        m.addSourceLine("return this._children;");
        return m;
    }
    
    Method newGetAttributesMethod() {
        GenericType gt = TypeFactory.newGenericType(this, "java.util.Map", "Map");
        TypeArgument ta = TypeFactory.newTypeArgument(TypeFactory.newReferenceType(
                this, "javax.xml.namespace.QName", "QName"));
        gt.addTypeArgument(ta);
        ta = TypeFactory.newTypeArgument(TypeFactory.newReferenceType(
                this, "java.lang.String", "String"));
        gt.addTypeArgument(ta);
        Method m = SourceFactory.newMethod("getAttributes", gt);
        m.setAccessLevel(AccessLevel.PUBLIC);
        m.addSourceLine("return this._attributes;");
        return m;
    }
    
    Method newGetParseHandlerMethod() {
        Type t = TypeFactory.newReferenceType(this, "org.xml.sax.ContentHandler", "ContentHandler");
        Method m = SourceFactory.newMethod("getParseHandler", t);
        m.setAccessLevel(AccessLevel.PUBLIC);
        m.addSourceLine("return this._handler;");
        return m;
    }
    
    public DocumentClass getDocument() {
        return document;
    }
    
    public Interface getChildInterface() {
        return childInterface;
    }

    public Interface getElementInterface() {
        return elementInterface;
    }
    
    public void addClass(ClassType cls) {
        super.addClass(cls);
        if (cls instanceof InterfaceImplPair) {
            // add all of the xml stuff to the class
            setupInterfaceImplPair((InterfaceImplPair) cls);
            // check if this is the root type
            Object o = cls.getOriginatingType().getExtendedInfo().get("xml");
            if (o instanceof ExtendedInfo) {
                ExtendedInfo ei = (ExtendedInfo) o;
                if (ei.isRoot() && (parser == null)) {
                    parser = new ParserClass(this, cls);
                    addClass(parser);
                }
            }
        }
    }

    private void setupInterfaceImplPair(InterfaceImplPair iip) {
        // Does the implementation class have another element as its superclass?
        boolean hasSuperClass = false;
        hasSuperClass = false;
        ReferenceType superType = iip.getSuperClass();
        if ((superType != null) && superType.hasImplementation()) {
            ClassType superClass = superType.getClassType();
            Collection<ReferenceType> ifcs;
            if (superClass instanceof InterfaceImplPair)
                ifcs = ((InterfaceImplPair) superClass).getImplSuperInterfaces(); else
                ifcs = superClass.getSuperInterfaces();
            for (ReferenceType ifc : ifcs) {
                if (ifc.hasImplementation() && (ifc.getClassType() == getElementInterface()))
                    hasSuperClass = true;
            }
        }
        // Superinterface
        iip.addImplSuperInterface(TypeFactory.newReferenceType(getElementInterface()));
        // Methods
        iip.addImplMethod(newGetDocumentMethod());
        iip.addImplMethod(newGetParentMethod());
        iip.addImplMethod(newGetTagNameMethod());
        iip.addImplMethod(newSetTagNameMethod());
        iip.addImplMethod(newGetChildrenMethod());
        iip.addImplMethod(newGetAttributesMethod());
        iip.addImplMethod(newGetParseHandlerMethod());
        // Fields
        Field f = SourceFactory.newField("_document", 
                TypeFactory.newReferenceType(getDocument()));
        f.setAccessLevel(AccessLevel.PRIVATE);
        iip.addField(f);
        f = SourceFactory.newField("_parent", 
                TypeFactory.newReferenceType(getElementInterface()));
        f.setAccessLevel(AccessLevel.PRIVATE);
        iip.addField(f);
        f = SourceFactory.newField("_tagName",
                TypeFactory.newReferenceType(this, "javax.xml.namespace.QName", "QName"));
        f.setAccessLevel(AccessLevel.PRIVATE);
        iip.addField(f);
        Class c = new MyChildrenClass(this, iip, hasSuperClass);
        f = SourceFactory.newField("_children", TypeFactory.newReferenceType(c));
        f.setAccessLevel(AccessLevel.PRIVATE);
        f.setDefaultExpression("new " + c.getFullName() + "()");
        iip.addField(f);
        iip.addImplNestedClass(c);
        c = new MyAttributesClass(this, iip, hasSuperClass);
        f = SourceFactory.newField("_attributes", TypeFactory.newReferenceType(this,
                "de.realityinabox.databinding.libs.AttributeMap", "AttributeMap"));
        f.setAccessLevel(AccessLevel.PRIVATE);
        f.setDefaultExpression("new de.realityinabox.databinding.libs.AttributeMap(new " +
                c.getFullName() + "())");
        iip.addField(f);
        iip.addImplNestedClass(c);
        c = new MyHandlerClass(this, iip, hasSuperClass);
        f = SourceFactory.newField("_handler", TypeFactory.newReferenceType(c));
        f.setAccessLevel(AccessLevel.PRIVATE);
        f.setDefaultExpression("new " + c.getFullName() + "()");
        iip.addField(f);
        iip.addImplNestedClass(c);
        // Constructor
        Constructor con = SourceFactory.newConstructor(iip);
        con.addArgument(SourceFactory.newVariable("document", 
                TypeFactory.newReferenceType(getDocument())));
        con.addArgument(SourceFactory.newVariable("parent",
                TypeFactory.newReferenceType(getElementInterface())));
        con.addSourceLine("this._document = document;");
        con.addSourceLine("this._parent = parent;");
    }
}

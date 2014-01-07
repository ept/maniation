package de.realityinabox.databinding.xml;

import java.util.Collection;

import javax.xml.namespace.QName;

import de.realityinabox.databinding.sourcemodel.AccessLevel;
import de.realityinabox.databinding.sourcemodel.Class;
import de.realityinabox.databinding.sourcemodel.ClassType;
import de.realityinabox.databinding.sourcemodel.InterfaceImplPair;
import de.realityinabox.databinding.sourcemodel.Method;
import de.realityinabox.databinding.sourcemodel.Property;
import de.realityinabox.databinding.sourcemodel.SourceFactory;
import de.realityinabox.databinding.types.IntegralType;
import de.realityinabox.databinding.types.Type;
import de.realityinabox.databinding.types.TypeFactory;

public class MyHandlerClass extends Class {

    private XMLPackage pack;
    private ClassType enclosingClass;
    private Collection<Elem> elements = new java.util.ArrayList<Elem>();

    MyHandlerClass(XMLPackage pack, InterfaceImplPair enclosingClass, boolean hasSuperClass) {
        super(pack, null, enclosingClass, "MyHandler");
        this.pack = pack;
        this.enclosingClass = enclosingClass;
        setSuperClass(TypeFactory.newReferenceType(pack,
                "org.xml.sax.helpers.DefaultHandler", "DefaultHandler"));
        setAccessLevel(AccessLevel.PRIVATE);
        determineElements();
        addStartElementMethod();
        addEndElementMethod();
        addCharactersMethod();
    }
    
    private void determineElements() {
        for (Property p : enclosingClass.getProperties()) {
            Object o = p.getType().getExtendedInfo().get("xml");
            if ((o != null) && (o instanceof ExtendedInfo)) {
                ExtendedInfo ei = (ExtendedInfo) o;
                if (ei.getRepresentation() == ExtendedInfo.Representation.CHILD)
                    elements.add(new Elem(ei.getName(), p.getType().getClassType()));
            }
        }
    }
    
    private void addStartElementMethod() {
        Method m = SourceFactory.newMethod("startElement", TypeFactory.newVoidType(pack));
        m.setAccessLevel(AccessLevel.PUBLIC);
        Type t = TypeFactory.newReferenceType(pack, "java.lang.String", "String");
        m.addArgument(SourceFactory.newVariable("namespaceURI", t));
        m.addArgument(SourceFactory.newVariable("localName", t));
        m.addArgument(SourceFactory.newVariable("qName", t));
        t = TypeFactory.newReferenceType(pack, "org.xml.sax.Attributes", "Attributes");
        m.addArgument(SourceFactory.newVariable("atts", t));
        m.addException(TypeFactory.newReferenceType(pack, "org.xml.sax.SAXException", "SAXException"));
        m.addSourceLine(pack.getElementInterface().getFullName() + " _el = null;");
        m.addSourceLine("getDocument().getParseStack().push(_el);");
        m.addSourceLine("if (_el == null) return;");
        defaultStartElementCode(m, "_el");
        addMethod(m);
    }
    
    static void defaultStartElementCode(Method m, String elementVariableName) {
        m.addSourceLine(elementVariableName + ".setTagName(new javax.xml.namespace.QName(namespaceURI, localName));");
        m.addSourceLine("for (int i=0; i < atts.getLength(); i++) {");
        m.addSourceLine("    javax.xml.namespace.QName n = new javax.xml.namespace.QName(atts.getURI(i), atts.getLocalName(i));");
        m.addSourceLine("    " + elementVariableName + ".getAttributes().put(n, atts.getValue(i));");
        m.addSourceLine("}");
    }
    
    private void addEndElementMethod() {
        Method m = SourceFactory.newMethod("endElement", TypeFactory.newVoidType(pack));
        m.setAccessLevel(AccessLevel.PUBLIC);
        Type t = TypeFactory.newReferenceType(pack, "java.lang.String", "String");
        m.addArgument(SourceFactory.newVariable("namespaceURI", t));
        m.addArgument(SourceFactory.newVariable("localName", t));
        m.addArgument(SourceFactory.newVariable("qName", t));
        m.addException(TypeFactory.newReferenceType(pack, "org.xml.sax.SAXException", "SAXException"));
        m.addSourceLine("if (getParent() != null) getParent().getChildren().add(" +
                enclosingClass.getName() + ".this);");
        m.addSourceLine("getDocument().getParseStack().pop();");
        addMethod(m);
    }
    
    private void addCharactersMethod() {
        Method m = SourceFactory.newMethod("characters", TypeFactory.newVoidType(pack));
        m.setAccessLevel(AccessLevel.PUBLIC);
        Type t = TypeFactory.newArrayType(TypeFactory.newIntegralType(pack, IntegralType.Size.CHAR));
        m.addArgument(SourceFactory.newVariable("ch", t));
        t = TypeFactory.newIntegralType(pack, IntegralType.Size.INT);
        m.addArgument(SourceFactory.newVariable("start", t));
        m.addArgument(SourceFactory.newVariable("length", t));
        m.addException(TypeFactory.newReferenceType(pack, "org.xml.sax.SAXException", "SAXException"));
        m.addSourceLine("//getChildren().add(new CDataImpl(document, new String(ch, start, length)));");
        addMethod(m);
    }
    
/*
        public void startElement(String namespaceURI, String localName, String qName,
                org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            ElementImpl el = null;
            if (namespaceURI.equals(Document.NAMESPACE_URI)) {
                if (localName.equals("element"))     el = new ElementTag    (document, ElementImpl.this); else
                if (localName.equals("nsName"))      el = new NSNameTag     (document, ElementImpl.this); else
                                                     el = new ElementImpl   (document, ElementImpl.this);
            } else                                   el = new ElementImpl   (document, ElementImpl.this);
            el.setNamespaceContext(childNamespaceContext);
            if (!localName.equals("mixed")) el.setTagName(new QName(namespaceURI, localName));
            el.parseAttributes(atts);
            document.getParseStack().push(el);
        }

        public void endElement(String namespaceURI, String localName, String qName) throws org.xml.sax.SAXException {
            if (parent != null) parent.getChildren().add(ElementImpl.this);
            document.getParseStack().pop();
        }

        public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
            getChildren().add(new CDataImpl(document, new String(ch, start, length)));
        }
 */
    
    private class Elem {
        QName name;
        ClassType cls;
        Elem(QName name, ClassType cls) {
            this.name = name; this.cls = cls;
        }
    }
}

package de.realityinabox.databinding.xml;

import de.realityinabox.databinding.sourcemodel.AccessLevel;
import de.realityinabox.databinding.sourcemodel.Class;
import de.realityinabox.databinding.sourcemodel.Constructor;
import de.realityinabox.databinding.sourcemodel.Field;
import de.realityinabox.databinding.sourcemodel.Method;
import de.realityinabox.databinding.sourcemodel.SourceFactory;
import de.realityinabox.databinding.sourcemodel.Tools;
import de.realityinabox.databinding.types.GenericType;
import de.realityinabox.databinding.types.KeyType;
import de.realityinabox.databinding.types.ReferenceType;
import de.realityinabox.databinding.types.Type;
import de.realityinabox.databinding.types.TypeFactory;

public class DocumentClass extends Class {
    
    private XMLPackage pack;

    public DocumentClass(XMLPackage pack) {
        super(pack, null, "XMLDocument");
        this.pack = pack;
        addSuperInterface(TypeFactory.newReferenceType(pack,
                "de.realityinabox.databinding.libs.Document", "Document"));
        addField(SourceFactory.newField("_root", TypeFactory.newReferenceType(pack,
                "de.realityinabox.databinding.libs.XMLElement", "XMLElement")));
        addField(SourceFactory.newField("_parseStack", TypeFactory.newReferenceType(pack,
                "de.realityinabox.databinding.libs.ParseStack", "ParseStack")));
        addConstructor();
        addGetParseStackMethod();
        addGetElementFactoryMethod();
        addGetRootMethod();
        addSetRootMethod();
        addNestedClass(new ContentInterceptorClass());
    }
    
    public void addKeyLookup(KeyType keyType, ReferenceType valueType) {
        String mapName = Tools.toJavaName(keyType.getKeyName(), true);
        GenericType mapType = TypeFactory.newGenericType(pack, "java.util.Map", "Map");
        mapType.addTypeArgument(TypeFactory.newTypeArgument(
                TypeFactory.newReferenceType(pack, keyType.getJavaName(), "")));
        mapType.addTypeArgument(TypeFactory.newTypeArgument(valueType));
        Field mapField = SourceFactory.newField("map" + mapName + "Keys", mapType);
        mapField.setDefaultExpression("new java.util.HashMap<" + keyType.getJavaName() + "," +
                valueType.getJavaName() + ">()");
        addField(mapField);
    }
    
    private void addConstructor() {
        Constructor c = SourceFactory.newConstructor(this);
        c.addSourceLine("_root = null;");
        c.addSourceLine("_parseStack = new de.realityinabox.databinding.libs.ParseStack();");
        c.addSourceLine("_parseStack.setHandler(new ContentInterceptor());");
    }
    
    private void addGetParseStackMethod() {
        Method m = SourceFactory.newMethod("getParseStack", TypeFactory.newReferenceType(pack, 
                "de.realityinabox.databinding.libs.ParseStack", "ParseStack"));
        m.setAccessLevel(AccessLevel.PUBLIC);
        m.addSourceLine("return _parseStack;");
        addMethod(m);
    }
    
    private void addGetElementFactoryMethod() {
        Method m = SourceFactory.newMethod("getElementFactory", TypeFactory.newReferenceType(pack, 
                "de.realityinabox.databinding.libs.ElementFactory", "ElementFactory"));
        m.setAccessLevel(AccessLevel.PUBLIC);
        m.addSourceLine("return null;");
        addMethod(m);
    }
    
    private void addGetRootMethod() {
        Method m = SourceFactory.newMethod("getRoot", TypeFactory.newReferenceType(pack,
                "de.realityinabox.databinding.libs.XMLElement", "XMLElement"));
        m.setAccessLevel(AccessLevel.PUBLIC);
        m.addSourceLine("return _root;");
        addMethod(m);
    }
    
    private void addSetRootMethod() {
        Method m = SourceFactory.newMethod("setRoot", TypeFactory.newVoidType(pack));
        m.setAccessLevel(AccessLevel.PUBLIC);
        Type t = TypeFactory.newReferenceType(pack,
                "de.realityinabox.databinding.libs.XMLElement", "XMLElement");
        m.addArgument(SourceFactory.newVariable("root", t));
        m.addSourceLine("this._root = root;");
        addMethod(m);
    }
    
    private class ContentInterceptorClass extends Class {
        
        ContentInterceptorClass() {
            super(pack, null, "ContentInterceptor");
            setSuperClass(TypeFactory.newReferenceType(pack, 
                    "de.realityinabox.databinding.libs.DelegateHandler", "DelegateHandler"));
            setAccessLevel(AccessLevel.PRIVATE);
            addStartDocumentMethod();
            addStartElementMethod();
        }
        
        private void addStartDocumentMethod() {
            Method m = SourceFactory.newMethod("startDocument", TypeFactory.newVoidType(pack));
            m.setAccessLevel(AccessLevel.PUBLIC);
            m.addSourceLine("_root = null;");
            addMethod(m);
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
            m.addSourceLine("super.startElement(namespaceURI, localName, qName, atts);");
            m.addSourceLine("if (_root != null) return;");
            //...
            MyHandlerClass.defaultStartElementCode(m, "_root");
            m.addSourceLine("_parseStack.push(_root);");
            addMethod(m);
        }
/*
    class ContentInterceptor extends DelegateHandler {
        public void startDocument() {
            root = null;
            namespaceContext = new NamespaceContext(DocumentImpl.this);
        }

        public void startElement(String uri, String localName, String qName,
                org.xml.sax.Attributes attributes) throws org.xml.sax.SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (root != null) return;
            if (uri.equals(Document.NAMESPACE_URI)) {
                if (localName.equals("element"))     root = new ElementTag    (DocumentImpl.this, null); else
                if (localName.equals("nsName"))      root = new NSNameTag     (DocumentImpl.this, null); else
                                                     root = new ElementImpl   (DocumentImpl.this, null);
            } else                                   root = new ElementImpl   (DocumentImpl.this, null);
            root.setNamespaceContext(namespaceContext);
            root.setTagName(new QName(uri, localName));
            root.parseAttributes(attributes);
            parseStack.push(root);
        }

        public void startPrefixMapping(String prefix, String uri) throws org.xml.sax.SAXException {
            namespaceContext.addPrefixMapping(prefix, uri);
        }
    }
 */        
    }
}

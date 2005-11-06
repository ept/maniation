package de.kleppmann.maniation.scene;

class VertexImpl implements de.kleppmann.maniation.scene.Vertex, de.kleppmann.maniation.scene.XMLElement {
    
    private javax.xml.namespace.QName _tagName;
    private de.realityinabox.databinding.libs.AttributeMap _attributes = new de.realityinabox.databinding.libs.AttributeMap(new de.kleppmann.maniation.scene.VertexImpl.MyAttributes());
    private de.kleppmann.maniation.scene.XMLElement _parent;
    private de.kleppmann.maniation.scene.XMLDocument _document;
    private de.kleppmann.maniation.scene.VertexImpl.MyHandler _handler = new de.kleppmann.maniation.scene.VertexImpl.MyHandler();
    private de.kleppmann.maniation.scene.VertexImpl.MyChildren _children = new de.kleppmann.maniation.scene.VertexImpl.MyChildren();
    
    VertexImpl(de.kleppmann.maniation.scene.XMLDocument document, de.kleppmann.maniation.scene.XMLElement parent) {
        this._document = document;
        this._parent = parent;
    }
    private java.lang.String id;
    private de.kleppmann.maniation.scene.Vector position;
    private de.kleppmann.maniation.scene.Vector normal;
    private java.util.List<de.kleppmann.maniation.scene.Deform> deforms = new java.util.ArrayList<de.kleppmann.maniation.scene.Deform>();
    
    public java.lang.String getId() {
        return id;
    }
    
    public void setId(java.lang.String id) {
        _document.mapVertexKeys.remove(this.id); _document.mapVertexKeys.put(id, this);
        this.id = id;
    }
    
    public de.kleppmann.maniation.scene.Vector getPosition() {
        return position;
    }
    
    public void setPosition(de.kleppmann.maniation.scene.Vector position) {
        this.position = position;
    }
    
    public de.kleppmann.maniation.scene.Vector getNormal() {
        return normal;
    }
    
    public void setNormal(de.kleppmann.maniation.scene.Vector normal) {
        this.normal = normal;
    }
    
    public java.util.List<de.kleppmann.maniation.scene.Deform> getDeforms() {
        return deforms;
    }
    
    public de.realityinabox.databinding.libs.Document getDocument() {
        return this._document;
    }
    
    public de.kleppmann.maniation.scene.XMLElement getParent() {
        return this._parent;
    }
    
    public javax.xml.namespace.QName getTagName() {
        return this._tagName;
    }
    
    public void setTagName(javax.xml.namespace.QName tagName) {
        this._tagName = tagName;
    }
    
    public java.util.List<de.realityinabox.databinding.libs.XMLChild> getChildren() {
        return this._children;
    }
    
    public java.util.Map<javax.xml.namespace.QName, java.lang.String> getAttributes() {
        return this._attributes;
    }
    
    public org.xml.sax.ContentHandler getParseHandler() {
        return this._handler;
    }
    
    
    private class MyAttributes extends de.realityinabox.databinding.libs.AttributeSet {
        
        javax.xml.namespace.QName _idAttribute = new javax.xml.namespace.QName("", "id");
        
        MyAttributes() {
            super(null);
        }
        
        public int size() {
            return 1;
        }
        
        public java.lang.String add(javax.xml.namespace.QName key, java.lang.String value) {
            java.lang.String _result = null;
            if (key.equals(_idAttribute)) {
                _result = getId();
                setId(value);
            } else
            throw new java.lang.IllegalArgumentException("XML attribute '" + key.getLocalPart() + "' is unknown");
            return _result;
        }
        
        public javax.xml.namespace.QName getKey(int index) {
            if (index == 0) return _idAttribute;
            throw new IllegalArgumentException();
        }
        
        public java.lang.String getValue(int index) {
            if (index == 0) return getId();
            throw new IllegalArgumentException();
        }
        
        public void remove(int index) {
            if ((index >= 0) && (index < 1)) return;
            throw new IllegalArgumentException();
        }
    }
    
    
    private class MyChildren extends java.util.AbstractList<de.realityinabox.databinding.libs.XMLChild> {
        
        
        private int ownSize() {
            int _i = 0;
            return _i;
        }
        
        public de.kleppmann.maniation.scene.XMLChild get(int index) {
            throw new IllegalArgumentException();
        }
        
        public int size() {
            return ownSize();
        }
        
        public de.kleppmann.maniation.scene.XMLChild set(int index, de.kleppmann.maniation.scene.XMLChild element) {
            de.kleppmann.maniation.scene.XMLChild _result;
            try {
                throw new java.lang.IllegalArgumentException();
            } catch (java.lang.ClassCastException e) {
                throw new java.lang.IllegalArgumentException(e);
            }
        }
        
        public void add(int index, de.kleppmann.maniation.scene.XMLChild element) {
            if (element instanceof de.kleppmann.maniation.scene.XMLElement)
                throw new java.lang.IllegalArgumentException("XML element '" + 
                    ((de.kleppmann.maniation.scene.XMLElement) element).getTagName().getLocalPart() + "' is unknown");
        }
        
        public de.kleppmann.maniation.scene.XMLChild remove(int index) {
            throw new java.lang.IllegalArgumentException();
        }
    }
    
    
    private class MyHandler extends org.xml.sax.helpers.DefaultHandler {
        
        
        public void startElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            de.kleppmann.maniation.scene.XMLElement _el = null;
            getDocument().getParseStack().push(_el);
            if (_el == null) return;
            _el.setTagName(new javax.xml.namespace.QName(namespaceURI, localName));
            for (int i=0; i < atts.getLength(); i++) {
                javax.xml.namespace.QName n = new javax.xml.namespace.QName(atts.getURI(i), atts.getLocalName(i));
                _el.getAttributes().put(n, atts.getValue(i));
            }
        }
        
        public void endElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName) throws org.xml.sax.SAXException {
            if (getParent() != null) getParent().getChildren().add(VertexImpl.this);
            getDocument().getParseStack().pop();
        }
        
        public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
            //getChildren().add(new CDataImpl(document, new String(ch, start, length)));
        }
    }
}

package de.kleppmann.maniation.scene;

class QuaternionImpl implements de.kleppmann.maniation.scene.Quaternion, de.kleppmann.maniation.scene.XMLElement {
    
    private javax.xml.namespace.QName _tagName;
    private de.realityinabox.databinding.libs.AttributeMap _attributes = new de.realityinabox.databinding.libs.AttributeMap(new de.kleppmann.maniation.scene.QuaternionImpl.MyAttributes());
    private de.kleppmann.maniation.scene.XMLElement _parent;
    private de.kleppmann.maniation.scene.XMLDocument _document;
    private de.kleppmann.maniation.scene.QuaternionImpl.MyHandler _handler = new de.kleppmann.maniation.scene.QuaternionImpl.MyHandler();
    private de.kleppmann.maniation.scene.QuaternionImpl.MyChildren _children = new de.kleppmann.maniation.scene.QuaternionImpl.MyChildren();
    
    QuaternionImpl(de.kleppmann.maniation.scene.XMLDocument document, de.kleppmann.maniation.scene.XMLElement parent) {
        this._document = document;
        this._parent = parent;
    }
    private double x;
    private double y;
    private double z;
    private double w;
    
    public double getX() {
        return x;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getY() {
        return y;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public double getZ() {
        return z;
    }
    
    public void setZ(double z) {
        this.z = z;
    }
    
    public double getW() {
        return w;
    }
    
    public void setW(double w) {
        this.w = w;
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
        
        javax.xml.namespace.QName _wAttribute = new javax.xml.namespace.QName("", "w");
        javax.xml.namespace.QName _zAttribute = new javax.xml.namespace.QName("", "z");
        javax.xml.namespace.QName _xAttribute = new javax.xml.namespace.QName("", "x");
        javax.xml.namespace.QName _yAttribute = new javax.xml.namespace.QName("", "y");
        
        MyAttributes() {
            super(null);
        }
        
        public int size() {
            return 4;
        }
        
        public java.lang.String add(javax.xml.namespace.QName key, java.lang.String value) {
            java.lang.String _result = null;
            if (key.equals(_xAttribute)) {
                _result = java.lang.Double.toString(getX());
                setX(java.lang.Double.parseDouble(value));
            } else
            if (key.equals(_yAttribute)) {
                _result = java.lang.Double.toString(getY());
                setY(java.lang.Double.parseDouble(value));
            } else
            if (key.equals(_zAttribute)) {
                _result = java.lang.Double.toString(getZ());
                setZ(java.lang.Double.parseDouble(value));
            } else
            if (key.equals(_wAttribute)) {
                _result = java.lang.Double.toString(getW());
                setW(java.lang.Double.parseDouble(value));
            } else
            throw new java.lang.IllegalArgumentException("XML attribute '" + key.getLocalPart() + "' is unknown");
            return _result;
        }
        
        public javax.xml.namespace.QName getKey(int index) {
            if (index == 0) return _xAttribute;
            if (index == 1) return _yAttribute;
            if (index == 2) return _zAttribute;
            if (index == 3) return _wAttribute;
            throw new IllegalArgumentException();
        }
        
        public java.lang.String getValue(int index) {
            if (index == 0) return java.lang.Double.toString(getX());
            if (index == 1) return java.lang.Double.toString(getY());
            if (index == 2) return java.lang.Double.toString(getZ());
            if (index == 3) return java.lang.Double.toString(getW());
            throw new IllegalArgumentException();
        }
        
        public void remove(int index) {
            if ((index >= 0) && (index < 4)) return;
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
            if (getParent() != null) getParent().getChildren().add(QuaternionImpl.this);
            getDocument().getParseStack().pop();
        }
        
        public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
            //getChildren().add(new CDataImpl(document, new String(ch, start, length)));
        }
    }
}

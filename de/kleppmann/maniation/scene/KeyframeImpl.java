package de.kleppmann.maniation.scene;

class KeyframeImpl implements de.kleppmann.maniation.scene.Keyframe, de.kleppmann.maniation.scene.XMLElement {
    
    private javax.xml.namespace.QName _tagName = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "keyframe");
    private de.realityinabox.databinding.libs.AttributeMap _attributes = new de.realityinabox.databinding.libs.AttributeMap(new de.kleppmann.maniation.scene.KeyframeImpl.MyAttributes());
    private de.kleppmann.maniation.scene.XMLElement _parent;
    private de.kleppmann.maniation.scene.XMLDocument _document;
    private de.kleppmann.maniation.scene.KeyframeImpl.MyHandler _handler = new de.kleppmann.maniation.scene.KeyframeImpl.MyHandler();
    private de.kleppmann.maniation.scene.KeyframeImpl.MyChildren _children = new de.kleppmann.maniation.scene.KeyframeImpl.MyChildren();
    
    KeyframeImpl(de.kleppmann.maniation.scene.XMLDocument document, de.kleppmann.maniation.scene.XMLElement parent) {
        this._document = document;
        this._parent = parent;
    }
    private double time;
    private de.kleppmann.maniation.scene.Quaternion rotation;
    
    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
    
    public de.kleppmann.maniation.scene.Quaternion getRotation() {
        return rotation;
    }
    
    public void setRotation(de.kleppmann.maniation.scene.Quaternion rotation) {
        this.rotation = rotation;
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
        
        javax.xml.namespace.QName _timeAttribute = new javax.xml.namespace.QName("", "time");
        
        MyAttributes() {
            super(null);
        }
        
        public int size() {
            return 1;
        }
        
        public java.lang.String add(javax.xml.namespace.QName key, java.lang.String value) {
            java.lang.String _result = null;
            if (key.equals(_timeAttribute)) {
                _result = java.lang.Double.toString(getTime());
                setTime(java.lang.Double.parseDouble(value));
            } else
            throw new java.lang.IllegalArgumentException("XML attribute '" + key.getLocalPart() + "' is unknown");
            return _result;
        }
        
        public javax.xml.namespace.QName getKey(int index) {
            if (index == 0) return _timeAttribute;
            throw new IllegalArgumentException();
        }
        
        public java.lang.String getValue(int index) {
            if (index == 0) return java.lang.Double.toString(getTime());
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
            if (getRotation() != null) _i++;
            return _i;
        }
        
        public de.realityinabox.databinding.libs.XMLChild get(int index) {
            try {
                if (getRotation() != null) {
                    if (index == 0) return (XMLChild) getRotation();
                    index--;
                }
            } catch (ClassCastException e) {
                assert(false);
            }
            throw new IllegalArgumentException();
        }
        
        public int size() {
            return ownSize();
        }
        
        public de.realityinabox.databinding.libs.XMLChild set(int index, de.realityinabox.databinding.libs.XMLChild element) {
            de.kleppmann.maniation.scene.XMLChild _result;
            try {
                if (getRotation() != null) {
                    if (index == 0) {
                        _result = (de.kleppmann.maniation.scene.XMLChild) getRotation();
                        setRotation((de.kleppmann.maniation.scene.QuaternionImpl) element);
                        return _result;
                    }
                    index--;
                }
                throw new java.lang.IllegalArgumentException();
            } catch (java.lang.ClassCastException e) {
                throw new java.lang.IllegalArgumentException(e);
            }
        }
        
        public void add(int index, de.realityinabox.databinding.libs.XMLChild element) {
            if (element instanceof de.kleppmann.maniation.scene.QuaternionImpl) {
                if (((de.kleppmann.maniation.scene.QuaternionImpl) element).getTagName().equals(_handler._rotationChild)) {
                    setRotation((de.kleppmann.maniation.scene.QuaternionImpl) element);
                    return;
                }
            }
            if (element instanceof de.kleppmann.maniation.scene.XMLElement)
                throw new java.lang.IllegalArgumentException("XML element '" + 
                    ((de.kleppmann.maniation.scene.XMLElement) element).getTagName().getLocalPart() + "' is unknown");
        }
        
        public de.realityinabox.databinding.libs.XMLChild remove(int index) {
            throw new java.lang.IllegalArgumentException();
        }
    }
    
    
    private class MyHandler extends org.xml.sax.helpers.DefaultHandler {
        
        javax.xml.namespace.QName _rotationChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "rotation");

        
        public void startElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            de.kleppmann.maniation.scene.XMLElement _el = null;
            if (namespaceURI.equals(_rotationChild.getNamespaceURI()) && localName.equals(_rotationChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.QuaternionImpl(_document, KeyframeImpl.this);
            getDocument().getParseStack().push(_el);
            if (_el == null) return;
            _el.setTagName(new javax.xml.namespace.QName(namespaceURI, localName));
            for (int i=0; i < atts.getLength(); i++) {
                javax.xml.namespace.QName n = new javax.xml.namespace.QName(atts.getURI(i), atts.getLocalName(i));
                _el.getAttributes().put(n, atts.getValue(i));
            }
        }
        
        public void endElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName) throws org.xml.sax.SAXException {
            if (getParent() != null) getParent().getChildren().add(KeyframeImpl.this);
            getDocument().getParseStack().pop();
        }
        
        public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
            //getChildren().add(new CDataImpl(document, new String(ch, start, length)));
        }
    }
}

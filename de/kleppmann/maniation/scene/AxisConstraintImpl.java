package de.kleppmann.maniation.scene;

class AxisConstraintImpl implements de.kleppmann.maniation.scene.AxisConstraint, de.kleppmann.maniation.scene.XMLElement {
    
    private javax.xml.namespace.QName _tagName;
    private de.realityinabox.databinding.libs.AttributeMap _attributes = new de.realityinabox.databinding.libs.AttributeMap(new de.kleppmann.maniation.scene.AxisConstraintImpl.MyAttributes());
    private de.kleppmann.maniation.scene.XMLElement _parent;
    private de.kleppmann.maniation.scene.XMLDocument _document;
    private de.kleppmann.maniation.scene.AxisConstraintImpl.MyHandler _handler = new de.kleppmann.maniation.scene.AxisConstraintImpl.MyHandler();
    private de.kleppmann.maniation.scene.AxisConstraintImpl.MyChildren _children = new de.kleppmann.maniation.scene.AxisConstraintImpl.MyChildren();
    
    AxisConstraintImpl(de.kleppmann.maniation.scene.XMLDocument document, de.kleppmann.maniation.scene.XMLElement parent) {
        this._document = document;
        this._parent = parent;
    }
    private double maxComfortable;
    private double minComfortable;
    private double maxExtreme;
    private double minExtreme;
    
    public double getMaxComfortable() {
        return maxComfortable;
    }
    
    public void setMaxComfortable(double maxComfortable) {
        this.maxComfortable = maxComfortable;
    }
    
    public double getMinComfortable() {
        return minComfortable;
    }
    
    public void setMinComfortable(double minComfortable) {
        this.minComfortable = minComfortable;
    }
    
    public double getMaxExtreme() {
        return maxExtreme;
    }
    
    public void setMaxExtreme(double maxExtreme) {
        this.maxExtreme = maxExtreme;
    }
    
    public double getMinExtreme() {
        return minExtreme;
    }
    
    public void setMinExtreme(double minExtreme) {
        this.minExtreme = minExtreme;
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
        
        javax.xml.namespace.QName _minComfortableAttribute = new javax.xml.namespace.QName("", "min-comfortable");
        javax.xml.namespace.QName _maxComfortableAttribute = new javax.xml.namespace.QName("", "max-comfortable");
        javax.xml.namespace.QName _maxExtremeAttribute = new javax.xml.namespace.QName("", "max-extreme");
        javax.xml.namespace.QName _minExtremeAttribute = new javax.xml.namespace.QName("", "min-extreme");
        
        MyAttributes() {
            super(null);
        }
        
        public int size() {
            return 4;
        }
        
        public java.lang.String add(javax.xml.namespace.QName key, java.lang.String value) {
            java.lang.String _result = null;
            if (key.equals(_maxComfortableAttribute)) {
                _result = java.lang.Double.toString(getMaxComfortable());
                setMaxComfortable(java.lang.Double.parseDouble(value));
            } else
            if (key.equals(_minComfortableAttribute)) {
                _result = java.lang.Double.toString(getMinComfortable());
                setMinComfortable(java.lang.Double.parseDouble(value));
            } else
            if (key.equals(_maxExtremeAttribute)) {
                _result = java.lang.Double.toString(getMaxExtreme());
                setMaxExtreme(java.lang.Double.parseDouble(value));
            } else
            if (key.equals(_minExtremeAttribute)) {
                _result = java.lang.Double.toString(getMinExtreme());
                setMinExtreme(java.lang.Double.parseDouble(value));
            } else
            throw new java.lang.IllegalArgumentException("XML attribute '" + key.getLocalPart() + "' is unknown");
            return _result;
        }
        
        public javax.xml.namespace.QName getKey(int index) {
            if (index == 0) return _maxComfortableAttribute;
            if (index == 1) return _minComfortableAttribute;
            if (index == 2) return _maxExtremeAttribute;
            if (index == 3) return _minExtremeAttribute;
            throw new IllegalArgumentException();
        }
        
        public java.lang.String getValue(int index) {
            if (index == 0) return java.lang.Double.toString(getMaxComfortable());
            if (index == 1) return java.lang.Double.toString(getMinComfortable());
            if (index == 2) return java.lang.Double.toString(getMaxExtreme());
            if (index == 3) return java.lang.Double.toString(getMinExtreme());
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
        
        public de.realityinabox.databinding.libs.XMLChild get(int index) {
            throw new IllegalArgumentException();
        }
        
        public int size() {
            return ownSize();
        }
        
        public de.realityinabox.databinding.libs.XMLChild set(int index, de.realityinabox.databinding.libs.XMLChild element) {
            try {
                throw new java.lang.IllegalArgumentException();
            } catch (java.lang.ClassCastException e) {
                throw new java.lang.IllegalArgumentException(e);
            }
        }
        
        public void add(int index, de.realityinabox.databinding.libs.XMLChild element) {
            if (element instanceof de.kleppmann.maniation.scene.XMLElement)
                throw new java.lang.IllegalArgumentException("XML element '" + 
                    ((de.kleppmann.maniation.scene.XMLElement) element).getTagName().getLocalPart() + "' is unknown");
        }
        
        public de.realityinabox.databinding.libs.XMLChild remove(int index) {
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
            if (getParent() != null) getParent().getChildren().add(AxisConstraintImpl.this);
            getDocument().getParseStack().pop();
        }
        
        public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
            //getChildren().add(new CDataImpl(document, new String(ch, start, length)));
        }
    }
}

package de.kleppmann.maniation.scene;

class BodyImpl implements de.kleppmann.maniation.scene.Body, de.kleppmann.maniation.scene.XMLElement {

    private javax.xml.namespace.QName _tagName = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "body");
    private de.realityinabox.databinding.libs.AttributeMap _attributes = new de.realityinabox.databinding.libs.AttributeMap(new de.kleppmann.maniation.scene.BodyImpl.MyAttributes());
    private de.kleppmann.maniation.scene.XMLElement _parent;
    private de.kleppmann.maniation.scene.XMLDocument _document;
    private de.kleppmann.maniation.scene.BodyImpl.MyHandler _handler = new de.kleppmann.maniation.scene.BodyImpl.MyHandler();
    private de.kleppmann.maniation.scene.BodyImpl.MyChildren _children = new de.kleppmann.maniation.scene.BodyImpl.MyChildren();
    
    BodyImpl(de.kleppmann.maniation.scene.XMLDocument document, de.kleppmann.maniation.scene.XMLElement parent) {
        this._document = document;
        this._parent = parent;
    }
    private java.lang.String name;
    private de.kleppmann.maniation.scene.Mesh mesh;
    private java.lang.String meshKeyRef;
    private boolean meshUpToDate = false;
    private boolean mobile;
    private de.kleppmann.maniation.scene.Vector location;
    private de.kleppmann.maniation.scene.Quaternion orientation;
    private de.kleppmann.maniation.scene.Vector axis;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Mesh getMesh() {
        if (!meshUpToDate) mesh = _document.mapMeshKeys.get(meshKeyRef);
        meshUpToDate = true;
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        meshUpToDate = true;
        this.mesh = mesh;
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public Vector getLocation() {
        return location;
    }

    public void setLocation(Vector location) {
        this.location = location;
    }

    public Quaternion getOrientation() {
        return orientation;
    }

    public void setOrientation(Quaternion orientation) {
        this.orientation = orientation;
    }

    public Vector getAxis() {
        return axis;
    }

    public void setAxis(Vector axis) {
        this.axis = axis;
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
        
        javax.xml.namespace.QName _nameAttribute = new javax.xml.namespace.QName("", "name");
        javax.xml.namespace.QName _meshAttribute = new javax.xml.namespace.QName("", "mesh-id");
        javax.xml.namespace.QName _mobileAttribute = new javax.xml.namespace.QName("", "mobile");
        
        MyAttributes() {
            super(null);
        }
        
        public int size() {
            return 3;
        }
        
        public java.lang.String add(javax.xml.namespace.QName key, java.lang.String value) {
            java.lang.String _result = null;
            if (key.equals(_nameAttribute)) {
                _result = getName();
                setName(value);
            } else
            if (key.equals(_meshAttribute)) {
                _result = (getMesh() == null ? "" : getMesh().getId());
                meshKeyRef = value; meshUpToDate = false;
            } else
            if (key.equals(_mobileAttribute)) {
                _result = (isMobile() ? "true" : "false");
                setMobile(value.equals("1") || value.equals("true"));
            } else
            throw new java.lang.IllegalArgumentException("XML attribute '" + key.getLocalPart() + "' is unknown");
            return _result;
        }
        
        public javax.xml.namespace.QName getKey(int index) {
            if (index == 0) return _nameAttribute;
            if (index == 1) return _meshAttribute;
            if (index == 2) return _mobileAttribute;
            throw new IllegalArgumentException();
        }
        
        public java.lang.String getValue(int index) {
            if (index == 0) return getName();
            if (index == 1) return (getMesh() == null ? "" : getMesh().getId());
            if (index == 2) return (isMobile() ? "true" : "false");
            throw new IllegalArgumentException();
        }
        
        public void remove(int index) {
            if ((index >= 0) && (index < 3)) return;
            throw new IllegalArgumentException();
        }
    }
    
    
    private class MyChildren extends java.util.AbstractList<de.realityinabox.databinding.libs.XMLChild> {
        
        
        private int ownSize() {
            int _i = 0;
            if (getLocation() != null) _i++;
            if (getOrientation() != null) _i++;
            return _i;
        }
        
        public de.realityinabox.databinding.libs.XMLChild get(int index) {
            try {
                if (getLocation() != null) {
                    if (index == 0) return (XMLChild) getLocation();
                    index--;
                }
                if (getOrientation() != null) {
                    if (index == 0) return (XMLChild) getOrientation();
                    index--;
                }
                if (getAxis() != null) {
                    if (index == 0) return (XMLChild) getAxis();
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
                if (getLocation() != null) {
                    if (index == 0) {
                        _result = (de.kleppmann.maniation.scene.XMLChild) getLocation();
                        setLocation((de.kleppmann.maniation.scene.VectorImpl) element);
                        return _result;
                    }
                    index--;
                }
                if (getOrientation() != null) {
                    if (index == 0) {
                        _result = (de.kleppmann.maniation.scene.XMLChild) getOrientation();
                        setOrientation((de.kleppmann.maniation.scene.QuaternionImpl) element);
                        return _result;
                    }
                    index--;
                }
                if (getAxis() != null) {
                    if (index == 0) {
                        _result = (de.kleppmann.maniation.scene.XMLChild) getAxis();
                        setAxis((de.kleppmann.maniation.scene.VectorImpl) element);
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
            if (element instanceof de.kleppmann.maniation.scene.VectorImpl) {
                if (((de.kleppmann.maniation.scene.VectorImpl) element).getTagName().equals(_handler._locationChild)) {
                    setLocation((de.kleppmann.maniation.scene.VectorImpl) element);
                    return;
                }
            }
            if (element instanceof de.kleppmann.maniation.scene.QuaternionImpl) {
                if (((de.kleppmann.maniation.scene.QuaternionImpl) element).getTagName().equals(_handler._orientationChild)) {
                    setOrientation((de.kleppmann.maniation.scene.QuaternionImpl) element);
                    return;
                }
            }
            if (element instanceof de.kleppmann.maniation.scene.VectorImpl) {
                if (((de.kleppmann.maniation.scene.VectorImpl) element).getTagName().equals(_handler._axisChild)) {
                    setAxis((de.kleppmann.maniation.scene.VectorImpl) element);
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
        
        javax.xml.namespace.QName _locationChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "location");
        javax.xml.namespace.QName _orientationChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "orientation");
        javax.xml.namespace.QName _axisChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "axis");

        
        public void startElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            de.kleppmann.maniation.scene.XMLElement _el = null;
            if (namespaceURI.equals(_locationChild.getNamespaceURI()) && localName.equals(_locationChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.VectorImpl(_document, BodyImpl.this);
            if (namespaceURI.equals(_orientationChild.getNamespaceURI()) && localName.equals(_orientationChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.QuaternionImpl(_document, BodyImpl.this);
            if (namespaceURI.equals(_axisChild.getNamespaceURI()) && localName.equals(_axisChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.VectorImpl(_document, BodyImpl.this);
            getDocument().getParseStack().push(_el);
            if (_el == null) return;
            _el.setTagName(new javax.xml.namespace.QName(namespaceURI, localName));
            for (int i=0; i < atts.getLength(); i++) {
                javax.xml.namespace.QName n = new javax.xml.namespace.QName(atts.getURI(i), atts.getLocalName(i));
                _el.getAttributes().put(n, atts.getValue(i));
            }
        }
        
        public void endElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName) throws org.xml.sax.SAXException {
            if (getParent() != null) getParent().getChildren().add(BodyImpl.this);
            getDocument().getParseStack().pop();
        }
        
        public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
            //getChildren().add(new CDataImpl(document, new String(ch, start, length)));
        }
    }
}

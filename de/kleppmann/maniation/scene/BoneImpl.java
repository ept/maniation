package de.kleppmann.maniation.scene;

class BoneImpl implements de.kleppmann.maniation.scene.Bone, de.kleppmann.maniation.scene.XMLElement {
    
    private javax.xml.namespace.QName _tagName;
    private de.realityinabox.databinding.libs.AttributeMap _attributes = new de.realityinabox.databinding.libs.AttributeMap(new de.kleppmann.maniation.scene.BoneImpl.MyAttributes());
    private de.kleppmann.maniation.scene.XMLElement _parent;
    private de.kleppmann.maniation.scene.XMLDocument _document;
    private de.kleppmann.maniation.scene.BoneImpl.MyHandler _handler = new de.kleppmann.maniation.scene.BoneImpl.MyHandler();
    private de.kleppmann.maniation.scene.BoneImpl.MyChildren _children = new de.kleppmann.maniation.scene.BoneImpl.MyChildren();
    
    BoneImpl(de.kleppmann.maniation.scene.XMLDocument document, de.kleppmann.maniation.scene.XMLElement parent) {
        this._document = document;
        this._parent = parent;
    }
    private java.lang.String id;
    private java.lang.String name;
    private de.kleppmann.maniation.scene.Bone parentBone;
    private java.lang.String parentBoneKeyRef;
    private boolean parentBoneUpToDate = false;
    private de.kleppmann.maniation.scene.Vector base;
    private de.kleppmann.maniation.scene.Quaternion orientation;
    private de.kleppmann.maniation.scene.Vector translationToLocal;
    private de.kleppmann.maniation.scene.Quaternion rotationToLocal;
    private de.kleppmann.maniation.scene.AxisConstraint xAxis;
    private de.kleppmann.maniation.scene.AxisConstraint yAxis;
    private de.kleppmann.maniation.scene.AxisConstraint zAxis;
    
    public java.lang.String getId() {
        return id;
    }
    
    public void setId(java.lang.String id) {
        _document.mapBoneKeys.remove(this.id); _document.mapBoneKeys.put(id, this);
        this.id = id;
    }
    
    public java.lang.String getName() {
        return name;
    }
    
    public void setName(java.lang.String name) {
        this.name = name;
    }
    
    public void setParentBone(de.kleppmann.maniation.scene.Bone parentBone) {
        parentBoneUpToDate = true;
        this.parentBone = parentBone;
    }
    
    public de.kleppmann.maniation.scene.Bone getParentBone() {
        if (!parentBoneUpToDate) parentBone = _document.mapBoneKeys.get(parentBoneKeyRef);
        parentBoneUpToDate = true;
        return parentBone;
    }
    
    public de.kleppmann.maniation.scene.Vector getBase() {
        return base;
    }
    
    public void setBase(de.kleppmann.maniation.scene.Vector base) {
        this.base = base;
    }
    
    public de.kleppmann.maniation.scene.Quaternion getOrientation() {
        return orientation;
    }
    
    public void setOrientation(de.kleppmann.maniation.scene.Quaternion orientation) {
        this.orientation = orientation;
    }
    
    public de.kleppmann.maniation.scene.Vector getTranslationToLocal() {
        return translationToLocal;
    }
    
    public void setTranslationToLocal(de.kleppmann.maniation.scene.Vector translationToLocal) {
        this.translationToLocal = translationToLocal;
    }
    
    public de.kleppmann.maniation.scene.Quaternion getRotationToLocal() {
        return rotationToLocal;
    }
    
    public void setRotationToLocal(de.kleppmann.maniation.scene.Quaternion rotationToLocal) {
        this.rotationToLocal = rotationToLocal;
    }
    
    public de.kleppmann.maniation.scene.AxisConstraint getXAxis() {
        return xAxis;
    }
    
    public void setXAxis(de.kleppmann.maniation.scene.AxisConstraint xAxis) {
        this.xAxis = xAxis;
    }
    
    public de.kleppmann.maniation.scene.AxisConstraint getYAxis() {
        return yAxis;
    }
    
    public void setYAxis(de.kleppmann.maniation.scene.AxisConstraint yAxis) {
        this.yAxis = yAxis;
    }
    
    public de.kleppmann.maniation.scene.AxisConstraint getZAxis() {
        return zAxis;
    }
    
    public void setZAxis(de.kleppmann.maniation.scene.AxisConstraint zAxis) {
        this.zAxis = zAxis;
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
        
        javax.xml.namespace.QName _parentBoneAttribute = new javax.xml.namespace.QName("", "parent-bone-id");
        javax.xml.namespace.QName _idAttribute = new javax.xml.namespace.QName("", "id");
        javax.xml.namespace.QName _nameAttribute = new javax.xml.namespace.QName("", "name");
        
        MyAttributes() {
            super(null);
        }
        
        public int size() {
            return 3;
        }
        
        public java.lang.String add(javax.xml.namespace.QName key, java.lang.String value) {
            java.lang.String _result = null;
            if (key.equals(_idAttribute)) {
                _result = getId();
                setId(value);
            } else
            if (key.equals(_nameAttribute)) {
                _result = getName();
                setName(value);
            } else
            if (key.equals(_parentBoneAttribute)) {
                _result = (getParentBone() == null ? "" : getParentBone().getId());
                parentBoneKeyRef = value; parentBoneUpToDate = false;
            } else
            throw new java.lang.IllegalArgumentException("XML attribute '" + key.getLocalPart() + "' is unknown");
            return _result;
        }
        
        public javax.xml.namespace.QName getKey(int index) {
            if (index == 0) return _idAttribute;
            if (index == 1) return _nameAttribute;
            if (index == 2) return _parentBoneAttribute;
            throw new IllegalArgumentException();
        }
        
        public java.lang.String getValue(int index) {
            if (index == 0) return getId();
            if (index == 1) return getName();
            if (index == 2) return (getParentBone() == null ? "" : getParentBone().getId());
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
            if (getParent() != null) getParent().getChildren().add(BoneImpl.this);
            getDocument().getParseStack().pop();
        }
        
        public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
            //getChildren().add(new CDataImpl(document, new String(ch, start, length)));
        }
    }
}

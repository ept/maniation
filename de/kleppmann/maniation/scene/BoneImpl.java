package de.kleppmann.maniation.scene;

class BoneImpl implements de.kleppmann.maniation.scene.Bone, de.kleppmann.maniation.scene.XMLElement {

    public de.kleppmann.maniation.maths.Quaternion getRotationAt(double time) {
        de.kleppmann.maniation.maths.Quaternion nothing =
            new de.kleppmann.maniation.maths.Quaternion(1, 0, 0, 0);
        if (getAnimation() == null) return nothing;
        // Adjust for looping animations
        time -= getAnimation().getStart();
        double length = getAnimation().getFinish() - getAnimation().getStart();
        if (!getAnimation().isLoop() && ((time < 0.0) || (time > length))) return nothing;
        if (getAnimation().isLoop()) {
            while (time < 0.0) time += length;
            while (time >= length) time -= length;
        }
        // Find nearest keyframe before and after current time
        double prevTime = -1e100, nextTime = +1e100;
        de.kleppmann.maniation.scene.Quaternion prevQuat = null, nextQuat = null;
        for (Keyframe kf : getAnimation().getKeyframes()) {
            if ((kf.getTime() < time) && (kf.getTime() > prevTime)) {
                prevTime = kf.getTime(); prevQuat = kf.getRotation();
            }
            if ((kf.getTime() >= time) && (kf.getTime() < nextTime)) {
                nextTime = kf.getTime(); nextQuat = kf.getRotation();
            }
        }
        if ((prevQuat == null) && (nextQuat == null)) return nothing;
        // Interpolate between quaternions if two are available, otherwise just return one of them
        de.kleppmann.maniation.maths.Quaternion prev = null;
        de.kleppmann.maniation.maths.Quaternion next = null;
        if (prevQuat != null) prev = new de.kleppmann.maniation.maths.Quaternion(
                prevQuat.getW(), prevQuat.getX(), prevQuat.getY(), prevQuat.getZ());
        if (nextQuat != null) next = new de.kleppmann.maniation.maths.Quaternion(
                nextQuat.getW(), nextQuat.getX(), nextQuat.getY(), nextQuat.getZ());
        if (prevQuat == null) return next;
        if (nextQuat == null) return prev;
        return prevQuat.getValue().interpolateTo(nextQuat.getValue(),
                (time - prevTime) / (nextTime - prevTime));
    }
    
    private javax.xml.namespace.QName _tagName = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "bone");
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
    private de.kleppmann.maniation.scene.Animation animation;
    
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

    public de.kleppmann.maniation.scene.Animation getAnimation() {
        return animation;
    }
    
    public void setAnimation(de.kleppmann.maniation.scene.Animation animation) {
        this.animation = animation;
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
            if (getBase() != null) _i++;
            if (getOrientation() != null) _i++;
            if (getTranslationToLocal() != null) _i++;
            if (getRotationToLocal() != null) _i++;
            if (getXAxis() != null) _i++;
            if (getYAxis() != null) _i++;
            if (getZAxis() != null) _i++;
            if (getAnimation() != null) _i++;
            return _i;
        }
        
        public de.realityinabox.databinding.libs.XMLChild get(int index) {
            try {
                if (getBase() != null) {
                    if (index == 0) return (XMLChild) getBase();
                    index--;
                }
                if (getOrientation() != null) {
                    if (index == 0) return (XMLChild) getOrientation();
                    index--;
                }
                if (getTranslationToLocal() != null) {
                    if (index == 0) return (XMLChild) getTranslationToLocal();
                    index--;
                }
                if (getRotationToLocal() != null) {
                    if (index == 0) return (XMLChild) getRotationToLocal();
                    index--;
                }
                if (getXAxis() != null) {
                    if (index == 0) return (XMLChild) getXAxis();
                    index--;
                }
                if (getYAxis() != null) {
                    if (index == 0) return (XMLChild) getYAxis();
                    index--;
                }
                if (getZAxis() != null) {
                    if (index == 0) return (XMLChild) getZAxis();
                    index--;
                }
                if (getAnimation() != null) {
                    if (index == 0) return (XMLChild) getAnimation();
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
                if (getBase() != null) {
                    if (index == 0) {
                        _result = (de.kleppmann.maniation.scene.XMLChild) getBase();
                        setBase((de.kleppmann.maniation.scene.VectorImpl) element);
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
                if (getTranslationToLocal() != null) {
                    if (index == 0) {
                        _result = (de.kleppmann.maniation.scene.XMLChild) getTranslationToLocal();
                        setTranslationToLocal((de.kleppmann.maniation.scene.VectorImpl) element);
                        return _result;
                    }
                    index--;
                }
                if (getRotationToLocal() != null) {
                    if (index == 0) {
                        _result = (de.kleppmann.maniation.scene.XMLChild) getRotationToLocal();
                        setRotationToLocal((de.kleppmann.maniation.scene.QuaternionImpl) element);
                        return _result;
                    }
                    index--;
                }
                if (getXAxis() != null) {
                    if (index == 0) {
                        _result = (de.kleppmann.maniation.scene.XMLChild) getXAxis();
                        setXAxis((de.kleppmann.maniation.scene.AxisConstraintImpl) element);
                        return _result;
                    }
                    index--;
                }
                if (getYAxis() != null) {
                    if (index == 0) {
                        _result = (de.kleppmann.maniation.scene.XMLChild) getYAxis();
                        setYAxis((de.kleppmann.maniation.scene.AxisConstraintImpl) element);
                        return _result;
                    }
                    index--;
                }
                if (getZAxis() != null) {
                    if (index == 0) {
                        _result = (de.kleppmann.maniation.scene.XMLChild) getZAxis();
                        setZAxis((de.kleppmann.maniation.scene.AxisConstraintImpl) element);
                        return _result;
                    }
                    index--;
                }
                if (getAnimation() != null) {
                    if (index == 0) {
                        _result = (de.kleppmann.maniation.scene.XMLChild) getAnimation();
                        setAnimation((de.kleppmann.maniation.scene.AnimationImpl) element);
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
                if (((de.kleppmann.maniation.scene.VectorImpl) element).getTagName().equals(_handler._baseChild)) {
                    setBase((de.kleppmann.maniation.scene.VectorImpl) element);
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
                if (((de.kleppmann.maniation.scene.VectorImpl) element).getTagName().equals(_handler._translationToLocalChild)) {
                    setTranslationToLocal((de.kleppmann.maniation.scene.VectorImpl) element);
                    return;
                }
            }
            if (element instanceof de.kleppmann.maniation.scene.QuaternionImpl) {
                if (((de.kleppmann.maniation.scene.QuaternionImpl) element).getTagName().equals(_handler._rotationToLocalChild)) {
                    setRotationToLocal((de.kleppmann.maniation.scene.QuaternionImpl) element);
                    return;
                }
            }
            if (element instanceof de.kleppmann.maniation.scene.AxisConstraintImpl) {
                if (((de.kleppmann.maniation.scene.AxisConstraintImpl) element).getTagName().equals(_handler._xAxisChild)) {
                    setXAxis((de.kleppmann.maniation.scene.AxisConstraintImpl) element);
                    return;
                }
            }
            if (element instanceof de.kleppmann.maniation.scene.AxisConstraintImpl) {
                if (((de.kleppmann.maniation.scene.AxisConstraintImpl) element).getTagName().equals(_handler._yAxisChild)) {
                    setYAxis((de.kleppmann.maniation.scene.AxisConstraintImpl) element);
                    return;
                }
            }
            if (element instanceof de.kleppmann.maniation.scene.AxisConstraintImpl) {
                if (((de.kleppmann.maniation.scene.AxisConstraintImpl) element).getTagName().equals(_handler._zAxisChild)) {
                    setZAxis((de.kleppmann.maniation.scene.AxisConstraintImpl) element);
                    return;
                }
            }
            if (element instanceof de.kleppmann.maniation.scene.AnimationImpl) {
                if (((de.kleppmann.maniation.scene.AnimationImpl) element).getTagName().equals(_handler._animationChild)) {
                    setAnimation((de.kleppmann.maniation.scene.AnimationImpl) element);
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
        
        javax.xml.namespace.QName _baseChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "base");
        javax.xml.namespace.QName _orientationChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "orientation");
        javax.xml.namespace.QName _translationToLocalChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "translation-to-local");
        javax.xml.namespace.QName _rotationToLocalChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "rotation-to-local");
        javax.xml.namespace.QName _xAxisChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "x-axis");
        javax.xml.namespace.QName _yAxisChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "y-axis");
        javax.xml.namespace.QName _zAxisChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "z-axis");
        javax.xml.namespace.QName _animationChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "animation");

        
        public void startElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            de.kleppmann.maniation.scene.XMLElement _el = null;
            if (namespaceURI.equals(_baseChild.getNamespaceURI()) && localName.equals(_baseChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.VectorImpl(_document, BoneImpl.this);
            if (namespaceURI.equals(_orientationChild.getNamespaceURI()) && localName.equals(_orientationChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.QuaternionImpl(_document, BoneImpl.this);
            if (namespaceURI.equals(_translationToLocalChild.getNamespaceURI()) && localName.equals(_translationToLocalChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.VectorImpl(_document, BoneImpl.this);
            if (namespaceURI.equals(_rotationToLocalChild.getNamespaceURI()) && localName.equals(_rotationToLocalChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.QuaternionImpl(_document, BoneImpl.this);
            if (namespaceURI.equals(_xAxisChild.getNamespaceURI()) && localName.equals(_xAxisChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.AxisConstraintImpl(_document, BoneImpl.this);
            if (namespaceURI.equals(_yAxisChild.getNamespaceURI()) && localName.equals(_yAxisChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.AxisConstraintImpl(_document, BoneImpl.this);
            if (namespaceURI.equals(_zAxisChild.getNamespaceURI()) && localName.equals(_zAxisChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.AxisConstraintImpl(_document, BoneImpl.this);
            if (namespaceURI.equals(_animationChild.getNamespaceURI()) && localName.equals(_animationChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.AnimationImpl(_document, BoneImpl.this);
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

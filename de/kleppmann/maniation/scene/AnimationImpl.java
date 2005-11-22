package de.kleppmann.maniation.scene;

class AnimationImpl implements de.kleppmann.maniation.scene.Animation, de.kleppmann.maniation.scene.XMLElement {
    
    private javax.xml.namespace.QName _tagName = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "animation");
    private de.realityinabox.databinding.libs.AttributeMap _attributes = new de.realityinabox.databinding.libs.AttributeMap(new de.kleppmann.maniation.scene.AnimationImpl.MyAttributes());
    private de.kleppmann.maniation.scene.XMLElement _parent;
    private de.kleppmann.maniation.scene.XMLDocument _document;
    private de.kleppmann.maniation.scene.AnimationImpl.MyHandler _handler = new de.kleppmann.maniation.scene.AnimationImpl.MyHandler();
    private de.kleppmann.maniation.scene.AnimationImpl.MyChildren _children = new de.kleppmann.maniation.scene.AnimationImpl.MyChildren();
    
    AnimationImpl(de.kleppmann.maniation.scene.XMLDocument document, de.kleppmann.maniation.scene.XMLElement parent) {
        this._document = document;
        this._parent = parent;
    }
    private double start;
    private double finish;
    private boolean loop;
    private java.util.List<de.kleppmann.maniation.scene.Keyframe> keyframes = new java.util.ArrayList<de.kleppmann.maniation.scene.Keyframe>();
    
    public double getStart() {
        return start;
    }
    
    public void setStart(double start) {
        this.start = start;
    }
    
    public double getFinish() {
        return finish;
    }
    
    public void setFinish(double finish) {
        this.finish = finish;
    }
    
    public boolean isLoop() {
        return loop;
    }
    
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
    
    public java.util.List<de.kleppmann.maniation.scene.Keyframe> getKeyframes() {
        return keyframes;
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
        
        javax.xml.namespace.QName _startAttribute = new javax.xml.namespace.QName("", "start");
        javax.xml.namespace.QName _finishAttribute = new javax.xml.namespace.QName("", "finish");
        javax.xml.namespace.QName _loopAttribute = new javax.xml.namespace.QName("", "loop");
        
        MyAttributes() {
            super(null);
        }
        
        public int size() {
            return 3;
        }
        
        public java.lang.String add(javax.xml.namespace.QName key, java.lang.String value) {
            java.lang.String _result = null;
            if (key.equals(_startAttribute)) {
                _result = java.lang.Double.toString(getStart());
                setStart(java.lang.Double.parseDouble(value));
            } else
            if (key.equals(_finishAttribute)) {
                _result = java.lang.Double.toString(getFinish());
                setFinish(java.lang.Double.parseDouble(value));
            } else
            if (key.equals(_loopAttribute)) {
                _result = (isLoop() ? "true" : "false");
                setLoop(value.equals("1") || value.equals("true"));
            } else
            throw new java.lang.IllegalArgumentException("XML attribute '" + key.getLocalPart() + "' is unknown");
            return _result;
        }
        
        public javax.xml.namespace.QName getKey(int index) {
            if (index == 0) return _startAttribute;
            if (index == 1) return _finishAttribute;
            if (index == 2) return _loopAttribute;
            throw new IllegalArgumentException();
        }
        
        public java.lang.String getValue(int index) {
            if (index == 0) return java.lang.Double.toString(getStart());
            if (index == 1) return java.lang.Double.toString(getFinish());
            if (index == 2) return (isLoop() ? "true" : "false");
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
            _i += keyframes.size();
            return _i;
        }
        
        public de.realityinabox.databinding.libs.XMLChild get(int index) {
            try {
                if ((index >= 0) && (index < keyframes.size())) return (XMLChild) keyframes.get(index);
                index -= keyframes.size();
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
                if ((index >= 0) && (index < keyframes.size())) {
                    _result = (de.kleppmann.maniation.scene.XMLChild) keyframes.get(index);
                    keyframes.set(index, (de.kleppmann.maniation.scene.KeyframeImpl) element);
                    return _result;
                }
                index -= keyframes.size();
                throw new java.lang.IllegalArgumentException();
            } catch (java.lang.ClassCastException e) {
                throw new java.lang.IllegalArgumentException(e);
            }
        }
        
        public void add(int index, de.realityinabox.databinding.libs.XMLChild element) {
            if (element instanceof de.kleppmann.maniation.scene.KeyframeImpl) {
                if (((de.kleppmann.maniation.scene.KeyframeImpl) element).getTagName().equals(_handler._keyframeChild)) {
                    keyframes.add((de.kleppmann.maniation.scene.KeyframeImpl) element);
                    return;
                }
            }
            if (element instanceof de.kleppmann.maniation.scene.XMLElement)
                throw new java.lang.IllegalArgumentException("XML element '" + 
                    ((de.kleppmann.maniation.scene.XMLElement) element).getTagName().getLocalPart() + "' is unknown");
        }
        
        public de.realityinabox.databinding.libs.XMLChild remove(int index) {
            try {
                if ((index >= 0) && (index < keyframes.size())) return (XMLChild) keyframes.remove(index);
                index -= keyframes.size();
            } catch (ClassCastException e) {
                assert(false);
            }
            throw new java.lang.IllegalArgumentException();
        }
    }
    
    
    private class MyHandler extends org.xml.sax.helpers.DefaultHandler {
        
        javax.xml.namespace.QName _keyframeChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "keyframe");
        
        public void startElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            de.kleppmann.maniation.scene.XMLElement _el = null;
            if (namespaceURI.equals(_keyframeChild.getNamespaceURI()) && localName.equals(_keyframeChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.KeyframeImpl(_document, AnimationImpl.this);
            getDocument().getParseStack().push(_el);
            if (_el == null) return;
            _el.setTagName(new javax.xml.namespace.QName(namespaceURI, localName));
            for (int i=0; i < atts.getLength(); i++) {
                javax.xml.namespace.QName n = new javax.xml.namespace.QName(atts.getURI(i), atts.getLocalName(i));
                _el.getAttributes().put(n, atts.getValue(i));
            }
        }
        
        public void endElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName) throws org.xml.sax.SAXException {
            if (getParent() != null) getParent().getChildren().add(AnimationImpl.this);
            getDocument().getParseStack().pop();
        }
        
        public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
            //getChildren().add(new CDataImpl(document, new String(ch, start, length)));
        }
    }
}

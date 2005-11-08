package de.kleppmann.maniation.scene;

class MaterialImpl implements de.kleppmann.maniation.scene.Material, de.kleppmann.maniation.scene.XMLElement {
    
    private javax.xml.namespace.QName _tagName = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "material");
    private de.realityinabox.databinding.libs.AttributeMap _attributes = new de.realityinabox.databinding.libs.AttributeMap(new de.kleppmann.maniation.scene.MaterialImpl.MyAttributes());
    private de.kleppmann.maniation.scene.XMLElement _parent;
    private de.kleppmann.maniation.scene.XMLDocument _document;
    private de.kleppmann.maniation.scene.MaterialImpl.MyHandler _handler = new de.kleppmann.maniation.scene.MaterialImpl.MyHandler();
    private de.kleppmann.maniation.scene.MaterialImpl.MyChildren _children = new de.kleppmann.maniation.scene.MaterialImpl.MyChildren();
    
    MaterialImpl(de.kleppmann.maniation.scene.XMLDocument document, de.kleppmann.maniation.scene.XMLElement parent) {
        this._document = document;
        this._parent = parent;
    }
    private java.lang.String id;
    private double shininess;
    private double density;
    private boolean hollow;
    private double elasticity;
    private double staticFriction;
    private double slidingFriction;
    private de.kleppmann.maniation.scene.Colour ambient;
    private de.kleppmann.maniation.scene.Colour diffuse;
    private de.kleppmann.maniation.scene.Colour specular;
    private de.kleppmann.maniation.scene.Colour emissive;
    
    public java.lang.String getId() {
        return id;
    }
    
    public void setId(java.lang.String id) {
        _document.mapMaterialKeys.remove(this.id); _document.mapMaterialKeys.put(id, this);
        this.id = id;
    }
    
    public double getShininess() {
        return shininess;
    }
    
    public void setShininess(double shininess) {
        this.shininess = shininess;
    }
    
    public double getDensity() {
        return density;
    }
    
    public void setDensity(double density) {
        this.density = density;
    }
    
    public boolean isHollow() {
        return hollow;
    }
    
    public void setHollow(boolean hollow) {
        this.hollow = hollow;
    }
    
    public double getElasticity() {
        return elasticity;
    }
    
    public void setElasticity(double elasticity) {
        this.elasticity = elasticity;
    }
    
    public double getStaticFriction() {
        return staticFriction;
    }
    
    public void setStaticFriction(double staticFriction) {
        this.staticFriction = staticFriction;
    }
    
    public double getSlidingFriction() {
        return slidingFriction;
    }
    
    public void setSlidingFriction(double slidingFriction) {
        this.slidingFriction = slidingFriction;
    }
    
    public de.kleppmann.maniation.scene.Colour getAmbient() {
        return ambient;
    }
    
    public void setAmbient(de.kleppmann.maniation.scene.Colour ambient) {
        this.ambient = ambient;
    }
    
    public de.kleppmann.maniation.scene.Colour getDiffuse() {
        return diffuse;
    }
    
    public void setDiffuse(de.kleppmann.maniation.scene.Colour diffuse) {
        this.diffuse = diffuse;
    }
    
    public de.kleppmann.maniation.scene.Colour getSpecular() {
        return specular;
    }
    
    public void setSpecular(de.kleppmann.maniation.scene.Colour specular) {
        this.specular = specular;
    }
    
    public de.kleppmann.maniation.scene.Colour getEmissive() {
        return emissive;
    }
    
    public void setEmissive(de.kleppmann.maniation.scene.Colour emissive) {
        this.emissive = emissive;
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
        javax.xml.namespace.QName _staticFrictionAttribute = new javax.xml.namespace.QName("", "static-friction");
        javax.xml.namespace.QName _densityAttribute = new javax.xml.namespace.QName("", "density");
        javax.xml.namespace.QName _slidingFrictionAttribute = new javax.xml.namespace.QName("", "sliding-friction");
        javax.xml.namespace.QName _shininessAttribute = new javax.xml.namespace.QName("", "shininess");
        javax.xml.namespace.QName _elasticityAttribute = new javax.xml.namespace.QName("", "elasticity");
        javax.xml.namespace.QName _hollowAttribute = new javax.xml.namespace.QName("", "hollow");
        
        MyAttributes() {
            super(null);
        }
        
        public int size() {
            return 7;
        }
        
        public java.lang.String add(javax.xml.namespace.QName key, java.lang.String value) {
            java.lang.String _result = null;
            if (key.equals(_idAttribute)) {
                _result = getId();
                setId(value);
            } else
            if (key.equals(_shininessAttribute)) {
                _result = java.lang.Double.toString(getShininess());
                setShininess(java.lang.Double.parseDouble(value));
            } else
            if (key.equals(_densityAttribute)) {
                _result = java.lang.Double.toString(getDensity());
                setDensity(java.lang.Double.parseDouble(value));
            } else
            if (key.equals(_hollowAttribute)) {
                _result = (isHollow() ? "true" : "false");
                setHollow(value.equals("1") || value.equals("true"));
            } else
            if (key.equals(_elasticityAttribute)) {
                _result = java.lang.Double.toString(getElasticity());
                setElasticity(java.lang.Double.parseDouble(value));
            } else
            if (key.equals(_staticFrictionAttribute)) {
                _result = java.lang.Double.toString(getStaticFriction());
                setStaticFriction(java.lang.Double.parseDouble(value));
            } else
            if (key.equals(_slidingFrictionAttribute)) {
                _result = java.lang.Double.toString(getSlidingFriction());
                setSlidingFriction(java.lang.Double.parseDouble(value));
            } else
            throw new java.lang.IllegalArgumentException("XML attribute '" + key.getLocalPart() + "' is unknown");
            return _result;
        }
        
        public javax.xml.namespace.QName getKey(int index) {
            if (index == 0) return _idAttribute;
            if (index == 1) return _shininessAttribute;
            if (index == 2) return _densityAttribute;
            if (index == 3) return _hollowAttribute;
            if (index == 4) return _elasticityAttribute;
            if (index == 5) return _staticFrictionAttribute;
            if (index == 6) return _slidingFrictionAttribute;
            throw new IllegalArgumentException();
        }
        
        public java.lang.String getValue(int index) {
            if (index == 0) return getId();
            if (index == 1) return java.lang.Double.toString(getShininess());
            if (index == 2) return java.lang.Double.toString(getDensity());
            if (index == 3) return (isHollow() ? "true" : "false");
            if (index == 4) return java.lang.Double.toString(getElasticity());
            if (index == 5) return java.lang.Double.toString(getStaticFriction());
            if (index == 6) return java.lang.Double.toString(getSlidingFriction());
            throw new IllegalArgumentException();
        }
        
        public void remove(int index) {
            if ((index >= 0) && (index < 7)) return;
            throw new IllegalArgumentException();
        }
    }
    
    
    private class MyChildren extends java.util.AbstractList<de.realityinabox.databinding.libs.XMLChild> {
        
        
        private int ownSize() {
            int _i = 0;
            if (getAmbient() != null) _i++;
            if (getDiffuse() != null) _i++;
            if (getSpecular() != null) _i++;
            if (getEmissive() != null) _i++;
            return _i;
        }
        
        public de.realityinabox.databinding.libs.XMLChild get(int index) {
            try {
                if (getAmbient() != null) {
                    if (index == 0) return (XMLChild) getAmbient();
                    index--;
                }
                if (getDiffuse() != null) {
                    if (index == 0) return (XMLChild) getDiffuse();
                    index--;
                }
                if (getSpecular() != null) {
                    if (index == 0) return (XMLChild) getSpecular();
                    index--;
                }
                if (getEmissive() != null) {
                    if (index == 0) return (XMLChild) getEmissive();
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
                if (getAmbient() != null) {
                    if (index == 0) {
                        _result = (de.kleppmann.maniation.scene.XMLChild) getAmbient();
                        setAmbient((de.kleppmann.maniation.scene.ColourImpl) element);
                        return _result;
                    }
                    index--;
                }
                if (getDiffuse() != null) {
                    if (index == 0) {
                        _result = (de.kleppmann.maniation.scene.XMLChild) getDiffuse();
                        setDiffuse((de.kleppmann.maniation.scene.ColourImpl) element);
                        return _result;
                    }
                    index--;
                }
                if (getSpecular() != null) {
                    if (index == 0) {
                        _result = (de.kleppmann.maniation.scene.XMLChild) getSpecular();
                        setSpecular((de.kleppmann.maniation.scene.ColourImpl) element);
                        return _result;
                    }
                    index--;
                }
                if (getEmissive() != null) {
                    if (index == 0) {
                        _result = (de.kleppmann.maniation.scene.XMLChild) getEmissive();
                        setEmissive((de.kleppmann.maniation.scene.ColourImpl) element);
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
            if (element instanceof de.kleppmann.maniation.scene.ColourImpl) {
                if (((de.kleppmann.maniation.scene.ColourImpl) element).getTagName().equals(_handler._ambientChild)) {
                    setAmbient((de.kleppmann.maniation.scene.ColourImpl) element);
                    return;
                }
            }
            if (element instanceof de.kleppmann.maniation.scene.ColourImpl) {
                if (((de.kleppmann.maniation.scene.ColourImpl) element).getTagName().equals(_handler._diffuseChild)) {
                    setDiffuse((de.kleppmann.maniation.scene.ColourImpl) element);
                    return;
                }
            }
            if (element instanceof de.kleppmann.maniation.scene.ColourImpl) {
                if (((de.kleppmann.maniation.scene.ColourImpl) element).getTagName().equals(_handler._specularChild)) {
                    setSpecular((de.kleppmann.maniation.scene.ColourImpl) element);
                    return;
                }
            }
            if (element instanceof de.kleppmann.maniation.scene.ColourImpl) {
                if (((de.kleppmann.maniation.scene.ColourImpl) element).getTagName().equals(_handler._emissiveChild)) {
                    setEmissive((de.kleppmann.maniation.scene.ColourImpl) element);
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
        
        javax.xml.namespace.QName _ambientChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "ambient");
        javax.xml.namespace.QName _diffuseChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "diffuse");
        javax.xml.namespace.QName _specularChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "specular");
        javax.xml.namespace.QName _emissiveChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "emissive");
        
        public void startElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            de.kleppmann.maniation.scene.XMLElement _el = null;
            if (namespaceURI.equals(_ambientChild.getNamespaceURI()) && localName.equals(_ambientChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.ColourImpl(_document, MaterialImpl.this);
            if (namespaceURI.equals(_diffuseChild.getNamespaceURI()) && localName.equals(_diffuseChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.ColourImpl(_document, MaterialImpl.this);
            if (namespaceURI.equals(_specularChild.getNamespaceURI()) && localName.equals(_specularChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.ColourImpl(_document, MaterialImpl.this);
            if (namespaceURI.equals(_emissiveChild.getNamespaceURI()) && localName.equals(_emissiveChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.ColourImpl(_document, MaterialImpl.this);
            getDocument().getParseStack().push(_el);
            if (_el == null) return;
            _el.setTagName(new javax.xml.namespace.QName(namespaceURI, localName));
            for (int i=0; i < atts.getLength(); i++) {
                javax.xml.namespace.QName n = new javax.xml.namespace.QName(atts.getURI(i), atts.getLocalName(i));
                _el.getAttributes().put(n, atts.getValue(i));
            }
        }
        
        public void endElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName) throws org.xml.sax.SAXException {
            if (getParent() != null) getParent().getChildren().add(MaterialImpl.this);
            getDocument().getParseStack().pop();
        }
        
        public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
            //getChildren().add(new CDataImpl(document, new String(ch, start, length)));
        }
    }
}

package de.kleppmann.maniation.scene;

class SceneImpl implements de.kleppmann.maniation.scene.Scene, de.kleppmann.maniation.scene.XMLElement {
    
    public javax.media.j3d.Node getJava3D() {
        javax.media.j3d.Group group = new javax.media.j3d.Group();
        for (Mesh m : getMeshes()) group.addChild(m.getJava3D());
        return group;
    }
    
    private javax.xml.namespace.QName _tagName = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "scene");
    private de.realityinabox.databinding.libs.AttributeMap _attributes = new de.realityinabox.databinding.libs.AttributeMap(new de.kleppmann.maniation.scene.SceneImpl.MyAttributes());
    private de.kleppmann.maniation.scene.XMLElement _parent;
    private de.kleppmann.maniation.scene.XMLDocument _document;
    private de.kleppmann.maniation.scene.SceneImpl.MyHandler _handler = new de.kleppmann.maniation.scene.SceneImpl.MyHandler();
    private de.kleppmann.maniation.scene.SceneImpl.MyChildren _children = new de.kleppmann.maniation.scene.SceneImpl.MyChildren();
    
    SceneImpl(de.kleppmann.maniation.scene.XMLDocument document, de.kleppmann.maniation.scene.XMLElement parent) {
        this._document = document;
        this._parent = parent;
    }
    private java.util.List<de.kleppmann.maniation.scene.Material> materials = new java.util.ArrayList<de.kleppmann.maniation.scene.Material>();
    private java.util.List<de.kleppmann.maniation.scene.Skeleton> skeletons = new java.util.ArrayList<de.kleppmann.maniation.scene.Skeleton>();
    private java.util.List<de.kleppmann.maniation.scene.Mesh> meshes = new java.util.ArrayList<de.kleppmann.maniation.scene.Mesh>();
    
    public java.util.List<de.kleppmann.maniation.scene.Material> getMaterials() {
        return materials;
    }
    
    public java.util.List<de.kleppmann.maniation.scene.Skeleton> getSkeletons() {
        return skeletons;
    }
    
    public java.util.List<de.kleppmann.maniation.scene.Mesh> getMeshes() {
        return meshes;
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
        
        
        MyAttributes() {
            super(null);
        }
        
        public int size() {
            return 0;
        }
        
        public java.lang.String add(javax.xml.namespace.QName key, java.lang.String value) {
            throw new java.lang.IllegalArgumentException("XML attribute '" + key.getLocalPart() + "' is unknown");
        }
        
        public javax.xml.namespace.QName getKey(int index) {
            throw new IllegalArgumentException();
        }
        
        public java.lang.String getValue(int index) {
            throw new IllegalArgumentException();
        }
        
        public void remove(int index) {
            if ((index >= 0) && (index < 0)) return;
            throw new IllegalArgumentException();
        }
    }
    
    
    private class MyChildren extends java.util.AbstractList<de.realityinabox.databinding.libs.XMLChild> {
        
        
        private int ownSize() {
            int _i = 0;
            _i += materials.size();
            _i += skeletons.size();
            _i += meshes.size();
            return _i;
        }
        
        public de.realityinabox.databinding.libs.XMLChild get(int index) {
            try {
                if ((index >= 0) && (index < materials.size())) return (XMLChild) materials.get(index);
                index -= materials.size();
                if ((index >= 0) && (index < skeletons.size())) return (XMLChild) skeletons.get(index);
                index -= skeletons.size();
                if ((index >= 0) && (index < meshes.size())) return (XMLChild) meshes.get(index);
                index -= meshes.size();
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
                if ((index >= 0) && (index < materials.size())) {
                    _result = (de.kleppmann.maniation.scene.XMLChild) materials.get(index);
                    materials.set(index, (de.kleppmann.maniation.scene.MaterialImpl) element);
                    return _result;
                }
                index -= materials.size();
                if ((index >= 0) && (index < skeletons.size())) {
                    _result = (de.kleppmann.maniation.scene.XMLChild) skeletons.get(index);
                    skeletons.set(index, (de.kleppmann.maniation.scene.SkeletonImpl) element);
                    return _result;
                }
                index -= skeletons.size();
                if ((index >= 0) && (index < meshes.size())) {
                    _result = (de.kleppmann.maniation.scene.XMLChild) meshes.get(index);
                    meshes.set(index, (de.kleppmann.maniation.scene.MeshImpl) element);
                    return _result;
                }
                index -= meshes.size();
                throw new java.lang.IllegalArgumentException();
            } catch (java.lang.ClassCastException e) {
                throw new java.lang.IllegalArgumentException(e);
            }
        }
        
        public void add(int index, de.realityinabox.databinding.libs.XMLChild element) {
            if (element instanceof de.kleppmann.maniation.scene.MaterialImpl) {
                if (((de.kleppmann.maniation.scene.MaterialImpl) element).getTagName().equals(_handler._materialChild)) {
                    materials.add((de.kleppmann.maniation.scene.MaterialImpl) element);
                    return;
                }
            }
            if (element instanceof de.kleppmann.maniation.scene.SkeletonImpl) {
                if (((de.kleppmann.maniation.scene.SkeletonImpl) element).getTagName().equals(_handler._skeletonChild)) {
                    skeletons.add((de.kleppmann.maniation.scene.SkeletonImpl) element);
                    return;
                }
            }
            if (element instanceof de.kleppmann.maniation.scene.MeshImpl) {
                if (((de.kleppmann.maniation.scene.MeshImpl) element).getTagName().equals(_handler._meshChild)) {
                    meshes.add((de.kleppmann.maniation.scene.MeshImpl) element);
                    return;
                }
            }
            if (element instanceof de.kleppmann.maniation.scene.XMLElement)
                throw new java.lang.IllegalArgumentException("XML element '" + 
                    ((de.kleppmann.maniation.scene.XMLElement) element).getTagName().getLocalPart() + "' is unknown");
        }
        
        public de.realityinabox.databinding.libs.XMLChild remove(int index) {
            try {
                if ((index >= 0) && (index < materials.size())) return (XMLChild) materials.remove(index);
                index -= materials.size();
                if ((index >= 0) && (index < skeletons.size())) return (XMLChild) skeletons.remove(index);
                index -= skeletons.size();
                if ((index >= 0) && (index < meshes.size())) return (XMLChild) meshes.remove(index);
                index -= meshes.size();
            } catch (ClassCastException e) {
                assert(false);
            }
            throw new java.lang.IllegalArgumentException();
        }
    }
    
    
    private class MyHandler extends org.xml.sax.helpers.DefaultHandler {
        
        javax.xml.namespace.QName _materialChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "material");
        javax.xml.namespace.QName _skeletonChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "skeleton");
        javax.xml.namespace.QName _meshChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "mesh");
        
        public void startElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            de.kleppmann.maniation.scene.XMLElement _el = null;
            if (namespaceURI.equals(_materialChild.getNamespaceURI()) && localName.equals(_materialChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.MaterialImpl(_document, SceneImpl.this);
            if (namespaceURI.equals(_skeletonChild.getNamespaceURI()) && localName.equals(_skeletonChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.SkeletonImpl(_document, SceneImpl.this);
            if (namespaceURI.equals(_meshChild.getNamespaceURI()) && localName.equals(_meshChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.MeshImpl(_document, SceneImpl.this);
            getDocument().getParseStack().push(_el);
            if (_el == null) return;
            _el.setTagName(new javax.xml.namespace.QName(namespaceURI, localName));
            for (int i=0; i < atts.getLength(); i++) {
                javax.xml.namespace.QName n = new javax.xml.namespace.QName(atts.getURI(i), atts.getLocalName(i));
                _el.getAttributes().put(n, atts.getValue(i));
            }
        }
        
        public void endElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName) throws org.xml.sax.SAXException {
            if (getParent() != null) getParent().getChildren().add(SceneImpl.this);
            getDocument().getParseStack().pop();
        }
        
        public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
            //getChildren().add(new CDataImpl(document, new String(ch, start, length)));
        }
    }
}

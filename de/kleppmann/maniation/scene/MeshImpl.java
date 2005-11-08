package de.kleppmann.maniation.scene;

class MeshImpl implements de.kleppmann.maniation.scene.Mesh, de.kleppmann.maniation.scene.XMLElement {
    
    public javax.media.j3d.Node getJava3D() {
        javax.media.j3d.TriangleArray geometry = new javax.media.j3d.TriangleArray(3*faces.size(),
                javax.media.j3d.TriangleArray.COORDINATES |
                javax.media.j3d.TriangleArray.NORMALS);
        float[] val = new float[3];
        for (int i=0; i<faces.size(); i++) {
            for (int j=0; j<3; j++) {
                de.kleppmann.maniation.scene.Vertex v = faces.get(i).getVertices().get(j);
                val[0] = (float) v.getPosition().getX();
                val[1] = (float) v.getPosition().getY();
                val[2] = (float) v.getPosition().getZ();
                geometry.setCoordinate(3*i+j, val);
                val[0] = (float) v.getNormal().getX();
                val[1] = (float) v.getNormal().getY();
                val[2] = (float) v.getNormal().getZ();
                geometry.setNormal(3*i+j, val);
            }
        }
        javax.media.j3d.Appearance appearance = new javax.media.j3d.Appearance();
        appearance.setMaterial(getMaterial().getJava3D());
        return new javax.media.j3d.Shape3D(geometry, appearance);
    }
    
    private javax.xml.namespace.QName _tagName = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "mesh");
    private de.realityinabox.databinding.libs.AttributeMap _attributes = new de.realityinabox.databinding.libs.AttributeMap(new de.kleppmann.maniation.scene.MeshImpl.MyAttributes());
    private de.kleppmann.maniation.scene.XMLElement _parent;
    private de.kleppmann.maniation.scene.XMLDocument _document;
    private de.kleppmann.maniation.scene.MeshImpl.MyHandler _handler = new de.kleppmann.maniation.scene.MeshImpl.MyHandler();
    private de.kleppmann.maniation.scene.MeshImpl.MyChildren _children = new de.kleppmann.maniation.scene.MeshImpl.MyChildren();
    
    MeshImpl(de.kleppmann.maniation.scene.XMLDocument document, de.kleppmann.maniation.scene.XMLElement parent) {
        this._document = document;
        this._parent = parent;
    }
    private java.lang.String id;
    private de.kleppmann.maniation.scene.Skeleton skeleton;
    private java.lang.String skeletonKeyRef;
    private boolean skeletonUpToDate = false;
    private de.kleppmann.maniation.scene.Material material;
    private java.lang.String materialKeyRef;
    private boolean materialUpToDate = false;
    private java.util.List<de.kleppmann.maniation.scene.Vertex> vertices = new java.util.ArrayList<de.kleppmann.maniation.scene.Vertex>();
    private java.util.List<de.kleppmann.maniation.scene.Face> faces = new java.util.ArrayList<de.kleppmann.maniation.scene.Face>();
    
    public java.lang.String getId() {
        return id;
    }
    
    public void setId(java.lang.String id) {
        _document.mapMeshKeys.remove(this.id); _document.mapMeshKeys.put(id, this);
        this.id = id;
    }
    
    public void setSkeleton(de.kleppmann.maniation.scene.Skeleton skeleton) {
        skeletonUpToDate = true;
        this.skeleton = skeleton;
    }
    
    public de.kleppmann.maniation.scene.Skeleton getSkeleton() {
        if (!skeletonUpToDate) skeleton = _document.mapSkeletonKeys.get(skeletonKeyRef);
        skeletonUpToDate = true;
        return skeleton;
    }
    
    public void setMaterial(de.kleppmann.maniation.scene.Material material) {
        materialUpToDate = true;
        this.material = material;
    }
    
    public de.kleppmann.maniation.scene.Material getMaterial() {
        if (!materialUpToDate) material = _document.mapMaterialKeys.get(materialKeyRef);
        materialUpToDate = true;
        return material;
    }
    
    public java.util.List<de.kleppmann.maniation.scene.Vertex> getVertices() {
        return vertices;
    }
    
    public java.util.List<de.kleppmann.maniation.scene.Face> getFaces() {
        return faces;
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
        
        javax.xml.namespace.QName _materialAttribute = new javax.xml.namespace.QName("", "material-id");
        javax.xml.namespace.QName _idAttribute = new javax.xml.namespace.QName("", "id");
        javax.xml.namespace.QName _skeletonAttribute = new javax.xml.namespace.QName("", "skeleton-id");
        
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
            if (key.equals(_skeletonAttribute)) {
                _result = (getSkeleton() == null ? "" : getSkeleton().getId());
                skeletonKeyRef = value; skeletonUpToDate = false;
            } else
            if (key.equals(_materialAttribute)) {
                _result = (getMaterial() == null ? "" : getMaterial().getId());
                materialKeyRef = value; materialUpToDate = false;
            } else
            throw new java.lang.IllegalArgumentException("XML attribute '" + key.getLocalPart() + "' is unknown");
            return _result;
        }
        
        public javax.xml.namespace.QName getKey(int index) {
            if (index == 0) return _idAttribute;
            if (index == 1) return _skeletonAttribute;
            if (index == 2) return _materialAttribute;
            throw new IllegalArgumentException();
        }
        
        public java.lang.String getValue(int index) {
            if (index == 0) return getId();
            if (index == 1) return (getSkeleton() == null ? "" : getSkeleton().getId());
            if (index == 2) return (getMaterial() == null ? "" : getMaterial().getId());
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
            _i += vertices.size();
            _i += faces.size();
            return _i;
        }
        
        public de.realityinabox.databinding.libs.XMLChild get(int index) {
            try {
                if ((index >= 0) && (index < vertices.size())) return (XMLChild) vertices.get(index);
                index -= vertices.size();
                if ((index >= 0) && (index < faces.size())) return (XMLChild) faces.get(index);
                index -= faces.size();
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
                if ((index >= 0) && (index < vertices.size())) {
                    _result = (de.kleppmann.maniation.scene.XMLChild) vertices.get(index);
                    vertices.set(index, (de.kleppmann.maniation.scene.VertexImpl) element);
                    return _result;
                }
                index -= vertices.size();
                if ((index >= 0) && (index < faces.size())) {
                    _result = (de.kleppmann.maniation.scene.XMLChild) faces.get(index);
                    faces.set(index, (de.kleppmann.maniation.scene.FaceImpl) element);
                    return _result;
                }
                index -= faces.size();
                throw new java.lang.IllegalArgumentException();
            } catch (java.lang.ClassCastException e) {
                throw new java.lang.IllegalArgumentException(e);
            }
        }
        
        public void add(int index, de.realityinabox.databinding.libs.XMLChild element) {
            if (element instanceof de.kleppmann.maniation.scene.VertexImpl) {
                if (((de.kleppmann.maniation.scene.VertexImpl) element).getTagName().equals(_handler._vertexChild)) {
                    vertices.add((de.kleppmann.maniation.scene.VertexImpl) element);
                    return;
                }
            }
            if (element instanceof de.kleppmann.maniation.scene.FaceImpl) {
                if (((de.kleppmann.maniation.scene.FaceImpl) element).getTagName().equals(_handler._faceChild)) {
                    faces.add((de.kleppmann.maniation.scene.FaceImpl) element);
                    return;
                }
            }
            if (element instanceof de.kleppmann.maniation.scene.XMLElement)
                throw new java.lang.IllegalArgumentException("XML element '" + 
                    ((de.kleppmann.maniation.scene.XMLElement) element).getTagName().getLocalPart() + "' is unknown");
        }
        
        public de.realityinabox.databinding.libs.XMLChild remove(int index) {
            try {
                if ((index >= 0) && (index < vertices.size())) return (XMLChild) vertices.remove(index);
                index -= vertices.size();
                if ((index >= 0) && (index < faces.size())) return (XMLChild) faces.remove(index);
                index -= faces.size();
            } catch (ClassCastException e) {
                assert(false);
            }
            throw new java.lang.IllegalArgumentException();
        }
    }
    
    
    private class MyHandler extends org.xml.sax.helpers.DefaultHandler {
        
        javax.xml.namespace.QName _vertexChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "vertex");
        javax.xml.namespace.QName _faceChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "face");
        
        public void startElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            de.kleppmann.maniation.scene.XMLElement _el = null;
            if (namespaceURI.equals(_vertexChild.getNamespaceURI()) && localName.equals(_vertexChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.VertexImpl(_document, MeshImpl.this);
            if (namespaceURI.equals(_faceChild.getNamespaceURI()) && localName.equals(_faceChild.getLocalPart()))
                _el = new de.kleppmann.maniation.scene.FaceImpl(_document, MeshImpl.this);
            getDocument().getParseStack().push(_el);
            if (_el == null) return;
            _el.setTagName(new javax.xml.namespace.QName(namespaceURI, localName));
            for (int i=0; i < atts.getLength(); i++) {
                javax.xml.namespace.QName n = new javax.xml.namespace.QName(atts.getURI(i), atts.getLocalName(i));
                _el.getAttributes().put(n, atts.getValue(i));
            }
        }
        
        public void endElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName) throws org.xml.sax.SAXException {
            if (getParent() != null) getParent().getChildren().add(MeshImpl.this);
            getDocument().getParseStack().pop();
        }
        
        public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
            //getChildren().add(new CDataImpl(document, new String(ch, start, length)));
        }
    }
}

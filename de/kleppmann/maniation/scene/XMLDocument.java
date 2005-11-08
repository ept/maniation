package de.kleppmann.maniation.scene;

class XMLDocument implements de.realityinabox.databinding.libs.Document {
    
    de.realityinabox.databinding.libs.XMLElement _root;
    java.util.Map<java.lang.String, de.kleppmann.maniation.scene.Vertex> mapVertexKeys = new java.util.HashMap<java.lang.String,de.kleppmann.maniation.scene.Vertex>();
    java.util.Map<java.lang.String, de.kleppmann.maniation.scene.Material> mapMaterialKeys = new java.util.HashMap<java.lang.String,de.kleppmann.maniation.scene.Material>();
    java.util.Map<java.lang.String, de.kleppmann.maniation.scene.Mesh> mapMeshKeys = new java.util.HashMap<java.lang.String,de.kleppmann.maniation.scene.Mesh>();
    java.util.Map<java.lang.String, de.kleppmann.maniation.scene.Skeleton> mapSkeletonKeys = new java.util.HashMap<java.lang.String,de.kleppmann.maniation.scene.Skeleton>();
    de.realityinabox.databinding.libs.ParseStack _parseStack;
    java.util.Map<java.lang.String, de.kleppmann.maniation.scene.Bone> mapBoneKeys = new java.util.HashMap<java.lang.String,de.kleppmann.maniation.scene.Bone>();
    
    XMLDocument() {
        _root = null;
        _parseStack = new de.realityinabox.databinding.libs.ParseStack();
        _parseStack.setHandler(new ContentInterceptor());
    }
    
    public de.realityinabox.databinding.libs.ParseStack getParseStack() {
        return _parseStack;
    }
    
    public de.realityinabox.databinding.libs.ElementFactory getElementFactory() {
        return null;
    }
    
    public de.realityinabox.databinding.libs.XMLElement getRoot() {
        return _root;
    }
    
    public void setRoot(de.realityinabox.databinding.libs.XMLElement root) {
        this._root = root;
    }
    
    
    private class ContentInterceptor extends de.realityinabox.databinding.libs.DelegateHandler {
        
        javax.xml.namespace.QName _sceneChild = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "scene");
        
        public void startDocument() {
            _root = null;
        }
        
        public void startElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            super.startElement(namespaceURI, localName, qName, atts);
            if (_root != null) return;
            if (namespaceURI.equals(_sceneChild.getNamespaceURI()) && localName.equals(_sceneChild.getLocalPart()))
                _root = new de.kleppmann.maniation.scene.SceneImpl(XMLDocument.this, null);
            _root.setTagName(new javax.xml.namespace.QName(namespaceURI, localName));
            for (int i=0; i < atts.getLength(); i++) {
                javax.xml.namespace.QName n = new javax.xml.namespace.QName(atts.getURI(i), atts.getLocalName(i));
                _root.getAttributes().put(n, atts.getValue(i));
            }
            _parseStack.push(_root);
        }
    }
}

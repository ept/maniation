package de.kleppmann.maniation.scene;

class FaceImpl implements de.kleppmann.maniation.scene.Face, de.kleppmann.maniation.scene.XMLElement {
    
    private javax.xml.namespace.QName _tagName = new javax.xml.namespace.QName("http://kleppmann.de/maniation/scene", "face");
    private de.realityinabox.databinding.libs.AttributeMap _attributes = new de.realityinabox.databinding.libs.AttributeMap(new de.kleppmann.maniation.scene.FaceImpl.MyAttributes());
    private de.kleppmann.maniation.scene.XMLElement _parent;
    private de.kleppmann.maniation.scene.XMLDocument _document;
    private de.kleppmann.maniation.scene.FaceImpl.MyHandler _handler = new de.kleppmann.maniation.scene.FaceImpl.MyHandler();
    private de.kleppmann.maniation.scene.FaceImpl.MyChildren _children = new de.kleppmann.maniation.scene.FaceImpl.MyChildren();
    
    FaceImpl(de.kleppmann.maniation.scene.XMLDocument document, de.kleppmann.maniation.scene.XMLElement parent) {
        this._document = document;
        this._parent = parent;
    }
    private java.util.List<de.kleppmann.maniation.scene.Vertex> vertices = new java.util.ArrayList<de.kleppmann.maniation.scene.Vertex>();
    private java.lang.String verticesKeyRefs = "";
    private boolean verticesUpToDate = false;
    
    public java.util.List<de.kleppmann.maniation.scene.Vertex> getVertices() {
        if (!verticesUpToDate) {
            vertices.clear();
            java.util.StringTokenizer st = new java.util.StringTokenizer(verticesKeyRefs);
            while (st.hasMoreTokens()) vertices.add(_document.mapVertexKeys.get(st.nextToken()));
            verticesUpToDate = true;
        }
        return vertices;
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
        
        javax.xml.namespace.QName _verticesAttribute = new javax.xml.namespace.QName("", "vertex-ids");
                
        MyAttributes() {
            super(null);
        }
        
        public int size() {
            return 1;
        }
        
        public java.lang.String add(javax.xml.namespace.QName key, java.lang.String value) {
            java.lang.String _result = null;
            if (key.equals(_verticesAttribute)) {
                _result = "";
                for (de.kleppmann.maniation.scene.Vertex v : getVertices()) {
                    if (!_result.equals("")) _result += " ";
                    _result += v.getId();
                }
                verticesKeyRefs = value; verticesUpToDate = false;
            } else
            throw new java.lang.IllegalArgumentException("XML attribute '" + key.getLocalPart() + "' is unknown");
            return _result;
        }
        
        public javax.xml.namespace.QName getKey(int index) {
            if (index == 0) return _verticesAttribute;
            throw new IllegalArgumentException();
        }
        
        public java.lang.String getValue(int index) {
            if (index == 0) {
                String _result = "";
                for (de.kleppmann.maniation.scene.Vertex v : getVertices()) {
                    if (!_result.equals("")) _result += " ";
                    _result += v.getId();
                }
                return _result;
            }
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
            if (getParent() != null) getParent().getChildren().add(FaceImpl.this);
            getDocument().getParseStack().pop();
        }
        
        public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
            //getChildren().add(new CDataImpl(document, new String(ch, start, length)));
        }
    }
}

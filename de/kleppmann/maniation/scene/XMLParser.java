package de.kleppmann.maniation.scene;

public class XMLParser {
    
    private javax.xml.parsers.SAXParserFactory factory;
    
    public XMLParser(javax.xml.parsers.SAXParserFactory parserFactory) {
        this.factory = parserFactory;
        factory.setNamespaceAware(true);
    }
    
    public de.kleppmann.maniation.scene.Scene parse(java.io.File input) throws org.xml.sax.SAXException, javax.xml.parsers.ParserConfigurationException, java.io.IOException {
        XMLDocument document = new XMLDocument();
        factory.newSAXParser().parse(input, document.getParseStack().getHandler());
        de.realityinabox.databinding.libs.XMLElement root = document.getRoot();
        if (root instanceof de.kleppmann.maniation.scene.Scene) return (de.kleppmann.maniation.scene.Scene) root;
        return null;
    }
    
    public de.kleppmann.maniation.scene.Scene parse(org.xml.sax.InputSource input) throws org.xml.sax.SAXException, javax.xml.parsers.ParserConfigurationException, java.io.IOException {
        XMLDocument document = new XMLDocument();
        factory.newSAXParser().parse(input, document.getParseStack().getHandler());
        de.realityinabox.databinding.libs.XMLElement root = document.getRoot();
        if (root instanceof de.kleppmann.maniation.scene.Scene) return (de.kleppmann.maniation.scene.Scene) root;
        return null;
    }
    
    public de.kleppmann.maniation.scene.Scene parse(java.io.InputStream input) throws org.xml.sax.SAXException, javax.xml.parsers.ParserConfigurationException, java.io.IOException {
        XMLDocument document = new XMLDocument();
        factory.newSAXParser().parse(input, document.getParseStack().getHandler());
        de.realityinabox.databinding.libs.XMLElement root = document.getRoot();
        if (root instanceof de.kleppmann.maniation.scene.Scene) return (de.kleppmann.maniation.scene.Scene) root;
        return null;
    }
    
    public de.kleppmann.maniation.scene.Scene parse(java.io.InputStream input, java.lang.String systemId) throws org.xml.sax.SAXException, javax.xml.parsers.ParserConfigurationException, java.io.IOException {
        XMLDocument document = new XMLDocument();
        factory.newSAXParser().parse(input, document.getParseStack().getHandler(), systemId);
        de.realityinabox.databinding.libs.XMLElement root = document.getRoot();
        if (root instanceof de.kleppmann.maniation.scene.Scene) return (de.kleppmann.maniation.scene.Scene) root;
        return null;
    }
    
    public de.kleppmann.maniation.scene.Scene parse(java.lang.String uri) throws org.xml.sax.SAXException, javax.xml.parsers.ParserConfigurationException, java.io.IOException {
        XMLDocument document = new XMLDocument();
        factory.newSAXParser().parse(uri, document.getParseStack().getHandler());
        de.realityinabox.databinding.libs.XMLElement root = document.getRoot();
        if (root instanceof de.kleppmann.maniation.scene.Scene) return (de.kleppmann.maniation.scene.Scene) root;
        return null;
    }
}

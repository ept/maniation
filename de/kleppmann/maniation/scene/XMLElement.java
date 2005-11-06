package de.kleppmann.maniation.scene;

interface XMLElement extends de.kleppmann.maniation.scene.XMLChild, de.realityinabox.databinding.libs.XMLElement {
    de.kleppmann.maniation.scene.XMLElement getParent();
    javax.xml.namespace.QName getTagName();
    void setTagName(javax.xml.namespace.QName tagName);
    java.util.List<de.realityinabox.databinding.libs.XMLChild> getChildren();
    java.util.Map<javax.xml.namespace.QName, java.lang.String> getAttributes();
    org.xml.sax.ContentHandler getParseHandler();
}

package de.realityinabox.databinding.libs;

public interface Document {
    ParseStack getParseStack();
    ElementFactory getElementFactory();
    XMLElement getRoot();
    void setRoot(XMLElement root);
}

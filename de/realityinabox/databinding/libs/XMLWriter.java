package de.realityinabox.databinding.libs;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.util.Map;
import java.util.HashSet;

public class XMLWriter {

    private java.io.OutputStream output;
    private String charsetName;
    private int nsIndex = 1;

    public XMLWriter(Document doc, java.io.OutputStream output) throws java.io.IOException {
        this.output = output;
        this.charsetName = "UTF-8";
        writeString("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        writeDocumentRoot(doc.getRoot());
    }
    
    public XMLWriter(Object node, java.io.OutputStream output) throws java.io.IOException {
        this.output = output;
        this.charsetName = "UTF-8";
        if (node instanceof XMLElement) {
            writeString("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            writeDocumentRoot((XMLElement) node);
        } else throw new IllegalArgumentException();
    }

    private void writeString(String value) throws java.io.IOException {
        output.write(value.getBytes(charsetName));
    }

    private void writeEscapedString(String value) throws java.io.IOException {
        writeString(value.replace("&", "&amp;").replace("<", "&lt;").
                replace(">", "&gt;").replace("\"", "&quot;"));
    }

    private void writeAttribute(QName name, String value, NamespaceContext context) throws java.io.IOException {
        String prefix = "";
        if (!name.getNamespaceURI().equals(javax.xml.XMLConstants.NULL_NS_URI)) {
            prefix = context.getPrefix(name.getNamespaceURI());
            if (!prefix.equals("")) prefix += ":";
        }
        writeString(" " + prefix + name.getLocalPart() + "=\"");
        writeEscapedString(value);
        writeString("\"");
    }

    private void writeElement(XMLElement element, NamespaceContext context, int depth) throws java.io.IOException {
        for (int i=0; i<depth; i++) writeString("  ");
        String tagname = context.getPrefix(element.getTagName().getNamespaceURI());
        if (!tagname.equals("")) tagname += ":";
        tagname += element.getTagName().getLocalPart();
        writeString("<" + tagname);
        for (Map.Entry<QName,String> att : element.getAttributes().entrySet())
            writeAttribute(att.getKey(), att.getValue(), context);
        if (element.getChildren().isEmpty()) writeString("/>\n"); else {
            writeString(">");
            boolean newline = (element.getChildren().get(0) instanceof XMLElement);
            if (newline) writeString("\n");
            for (XMLChild c : element.getChildren()) writeChild(c, context, depth + 1);
            if (newline) for (int i=0; i<depth; i++) writeString("  ");
            writeString("</" + tagname + ">\n");
        }
    }

    private void writeCData(XMLCData value) throws java.io.IOException {
        writeEscapedString(value.getString());
    }

    private void writeChild(XMLChild child, NamespaceContext context, int depth) throws java.io.IOException {
        if (child instanceof XMLElement) writeElement((XMLElement) child, context, depth);
        if (child instanceof XMLCData) writeCData((XMLCData) child);
    }

    private void generatePrefixes(XMLElement root, XMLElement current, NamespaceContext context) {
        checkPrefixMapping(root, current.getTagName().getNamespaceURI(), context);
        HashSet<String> uris = new HashSet<String>();
        for (QName name : current.getAttributes().keySet()) uris.add(name.getNamespaceURI());
        for (String uri : uris) checkPrefixMapping(root, uri, context);
        for (XMLChild child : current.getChildren()) {
            if (child instanceof XMLElement) generatePrefixes(root, (XMLElement) child, context);
        }
    }

    private void writeDocumentRoot(XMLElement root) throws java.io.IOException {
        NamespaceContext context = new NamespaceContext(root.getDocument());
        for (Map.Entry<QName,String> att : root.getAttributes().entrySet()) {
            if (att.getKey().getLocalPart().equals(XMLConstants.XMLNS_ATTRIBUTE))
                context.addPrefixMapping(XMLConstants.DEFAULT_NS_PREFIX, att.getValue());
            if (att.getKey().getNamespaceURI().equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI))
                context.addPrefixMapping(att.getKey().getLocalPart(), att.getValue());
        }
        generatePrefixes(root, root, context);
        writeElement(root, context, 0);
    }

    private void checkPrefixMapping(XMLElement root, String namespaceURI, NamespaceContext context) {
        if (namespaceURI.equals(XMLConstants.XML_NS_URI) ||
            namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) return;
        // If this URI is already mapped, return
        if (context.getPrefix(namespaceURI) != null) return;
        // Is the default namespace still unused?
        QName xmlnsAttribute;
        if (context.getNamespaceURI(XMLConstants.DEFAULT_NS_PREFIX).equals(XMLConstants.NULL_NS_URI)) {
            // Use default namespace.
            context.addPrefixMapping(XMLConstants.DEFAULT_NS_PREFIX, namespaceURI);
            xmlnsAttribute = new QName(namespaceURI, XMLConstants.XMLNS_ATTRIBUTE);
        } else {
            // Default is already taken, so define a new namespace
            while (!context.getNamespaceURI("n" + nsIndex).equals(XMLConstants.NULL_NS_URI)) nsIndex++;
            context.addPrefixMapping("n" + nsIndex, namespaceURI);
            xmlnsAttribute = new QName(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "n" + nsIndex);
        }
        root.getAttributes().put(xmlnsAttribute, namespaceURI);
    }
}

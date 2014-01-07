package de.realityinabox.databinding.libs;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;
import org.xml.sax.ContentHandler;

/**
 * Representation of an XML element (tag). Also a node of the XML tree.
 */
public interface XMLElement extends Element, XMLChild {
    /**
     * Returns the parent of the element, or null if this is the root of the tree.
     */
    XMLElement getParent();

    /**
     * Returns the qualified name (namespace URI and local part) of the element.
     * The prefix is not used.
     */
    QName getTagName();

    /**
     * Changes the qualified name (namespace URI and local part) of the element.
     * The prefix is not used.
     */
    void setTagName(QName name);

    /**
     * Returns a list of all children of the element in order. The list is backed by
     * the contents of the implementing object and may be manipulated.
     * This may cause IllegalArgumentExceptions.
     */
    List<XMLChild> getChildren();

    /**
     * Returns a mapping from all qualified attribute names to their respective string
     * representations. The list is backed by the contents of the implementing
     * object and may be manipulated. This may cause IllegalArgumentExceptions.
     */
    Map<QName,String> getAttributes();

    /**
     * Returns a SAX content handler object which can handle all SAX events occuring within
     * the scope of this element. This includes creation of the appropriate children and
     * CData objects. Newly created children must be pushed onto the parse stack
     * (see Document.getParseStack) so that they become recipients of the SAX events. When
     * an endElement event is encountered this means the current element is ended, and
     * the top of the parse stack must be popped off.
     */
    ContentHandler getParseHandler();
}

package de.realityinabox.databinding.libs;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.XMLConstants;

/**
 * Stores a set of mappings from XML namespace prefixes to namespace URI strings.
 * The mapping may be queried in both directions, and new mappings may be added.
 * NamespaceContext objects are intended for use only during XML marshalling
 * (XMLData.writeXML), where each represents the namespace mappings in scope at
 * a particular node in the XML tree.
 */
public class NamespaceContext implements javax.xml.namespace.NamespaceContext {

    private Document document;
    private HashMap<String,String> mapPrefixToURI;
    private HashMap<String,ArrayList<String>> mapURIToPrefix;
    private int nsIndex = 1;

    /**
     * Creates a new namespace context for use by the given document.
     * The context may not be used in conjunction with any other document.
     */
    public NamespaceContext(Document document) {
        this.document = document;
        mapPrefixToURI = new HashMap<String,String>();
        mapURIToPrefix = new HashMap<String,ArrayList<String>>();
        addPrefixMapping(XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI);
        addPrefixMapping(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        addPrefixMapping(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
    }
    
    /**
     * Looks up the URI referenced by a particular namespace prefix.
     * Returns the null URI (the empty string) if the prefix is unknown in
     * the current context. Behaviour is as specified by
     * javax.xml.namespace.NamespaceContext.getNamespaceURI().
     */
    public String getNamespaceURI(String prefix) {
        if (prefix == null) throw new IllegalArgumentException("null is not a valid argument");
        String s = mapPrefixToURI.get(prefix);
        if (s == null) return XMLConstants.NULL_NS_URI;
        return s;
    }

    /**
     * Returns a prefix which matches the given namespace URI.
     * If the default prefix (the empty string) is amongst the matching prefixes,
     * the default will be returned. Otherwise any matching prefix may be returned.
     * Behaviour is as specified by javax.xml.namespace.NamespaceContext.getPrefix().
     */
    public String getPrefix(String namespaceURI) {
        Iterator<String> i = getPrefixes(namespaceURI);
        if (!i.hasNext()) return null;
        boolean isDefault = false;
        String prefix = null;
        while (i.hasNext()) {
            prefix = i.next();
            if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) isDefault = true;
        }
        if (isDefault) return XMLConstants.DEFAULT_NS_PREFIX;
        return prefix;
    }

    /**
     * Returns an iterator over all the prefixes matching a given namespace URI.
     * Behaviour is as specified by javax.xml.namespace.NamespaceContext.getPrefixes().
     */
    public Iterator<String> getPrefixes(String namespaceURI) {
        if (namespaceURI == null) throw new IllegalArgumentException("null is not a valid argument");
        ArrayList<String> list = mapURIToPrefix.get(namespaceURI);
        if (list == null) return new EmptyIterator<String>();
        return list.iterator();
    }

    /**
     * Adds a prefix-URI pair to the namespace context.
     * If the prefix was previously already mapped, the previous mapping is replaced.
     * The default namespace is set by using XMLConstants.DEFAULT_NS_PREFIX
     * as value for prefix.
     */
    public void addPrefixMapping(String prefix, String namespaceURI) {
        if ((prefix == null) || (namespaceURI == null))
            throw new IllegalArgumentException("null is not a valid argument");
        String old = mapPrefixToURI.put(prefix, namespaceURI);
        if (old != null) {
            ArrayList<String> list = mapURIToPrefix.get(old);
            assert(list != null);
            list.remove(prefix);
        }
        ArrayList<String> list = mapURIToPrefix.get(namespaceURI);
        if (list == null) {
            list = new ArrayList<String>();
            mapURIToPrefix.put(namespaceURI, list);
        }
        list.add(prefix);
    }

    /**
     * Duplicates (clones) the namespace context. This method does basically what
     * you would expect of the clone method. Since calls to xmlnsAttribute()
     * may modify the context, the context must be duplicated before it is passed
     * down to children nodes and discarded afterwards, to ensure the prefix
     * mappings do not continue beyond their desired scope.
     */
    public NamespaceContext duplicate() {
        NamespaceContext c = new NamespaceContext(document);
        for (java.util.Map.Entry<String,String> item : mapPrefixToURI.entrySet())
            c.addPrefixMapping(item.getKey(), item.getValue());
        c.nsIndex = nsIndex;
        return c;
    }

    private class EmptyIterator<E> implements Iterator<E> {
        public boolean hasNext() { return false; }
        public E next() { throw new NoSuchElementException(""); }
        public void remove() { throw new UnsupportedOperationException(""); }
    }
}

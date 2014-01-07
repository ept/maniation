package de.realityinabox.databinding.libs;

/**
 * Super-interface for anything that may be contained inside a pair of opening
 * and closing XML tags: XML elements or character data.
 */
public interface XMLChild {
    /**
     * Returns the document to which the data element belongs.
     */
    Document getDocument();
}

package de.realityinabox.databinding.libs;

/**
 * Character data that may be contained between the opening and closing tags of
 * an element (not as value of an attribute).
 */
public interface XMLCData extends XMLChild {
    String getString();
}

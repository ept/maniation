package de.realityinabox.databinding.libs;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

public class AttributeMap extends AbstractMap<QName,String> {

    private AttributeSet entries;

    public AttributeMap(AttributeSet entries) {
        this.entries = entries;
    }

    public Set<Map.Entry<QName,String>> entrySet() {
        return entries;
    }

    public String put(QName key, String value) {
        return entries.add(key, value);
    }
}

package de.realityinabox.databinding.libs;

import javax.xml.namespace.QName;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AttributeSet extends AbstractSet<Map.Entry<QName,String>> {

    private AttributeSet superSet;
    
    protected AttributeSet(Set<Map.Entry<QName,String>> superSet) {
        try {
            this.superSet = (AttributeSet) superSet;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public int size() {
        assert(superSet != null);
        return superSet.size();
    }

    protected String add(QName key, String value) {
        assert(superSet != null);
        return superSet.add(key, value);
    }
    
    protected QName getKey(int index) {
        assert(superSet != null);
        return superSet.getKey(index);
    }
    
    protected String getValue(int index) {
        assert(superSet != null);
        return superSet.getValue(index);
    }
    
    protected void remove(int index) {
        assert(superSet != null);
        superSet.remove(index);
    }

    public Iterator<Map.Entry<QName,String>> iterator() {
        return new SetIterator();
    }

    private static class Entry implements Map.Entry<QName,String> {
        private QName key;
        private String value;

        Entry(QName key, String value) {
            this.key = key;
            this.value = value;
        }

        public boolean equals(Object o) {
            if (o instanceof Entry) {
                return ((Entry) o).getKey().equals(this.getKey()) &&
                    ((Entry) o).getValue().equals(this.getValue());
            }
            return false;
        }
        
        public QName getKey() {
            return key;
        }
        
        public String getValue() {
            return value;
        }
        
        public int hashCode() {
            return getKey().hashCode() ^ getValue().hashCode();
        }
        
        public String setValue(String value) {
            throw new UnsupportedOperationException();
        }
    }

    private class SetIterator implements Iterator<Map.Entry<QName,String>> {
        private int position = 0;
        private boolean removed = true;
        
        public boolean hasNext() {
            return position < size();
        }

        public Map.Entry<QName,String> next() {
            if (!hasNext()) throw new java.util.NoSuchElementException();
            Entry result = new Entry(getKey(position), getValue(position));
            position++; removed = false;
            return result;
        }

        public void remove() {
            if (removed) throw new IllegalStateException();
            AttributeSet.this.remove(position - 1);
        }
    }
}

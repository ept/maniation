package de.realityinabox.databinding.xml;

import javax.xml.namespace.QName;

import de.realityinabox.databinding.sourcemodel.Package;

public class ExtendedInfo {
    
    public enum Representation { ATTRIBUTE, CHILD, CDATA };

    private XMLPackage pack;
    private ExtendedInfo nestedInfo;
    private Representation repr;
    private QName name;
    private boolean root;

    public ExtendedInfo(Package pack, Object nestedInfo, Representation repr, QName name) {
        try {
            this.pack = (XMLPackage) pack;
            this.nestedInfo = (ExtendedInfo) nestedInfo;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
        this.repr = repr;
        this.name = name;
    }
    
    public XMLPackage getPackage() {
        return pack;
    }
    
    public ExtendedInfo getNestedInfo() {
        return nestedInfo;
    }

    public Representation getRepresentation() {
        return repr;
    }
    
    public QName getName() {
        return name;
    }
    
    public void setRoot(boolean root) {
        this.root = root;
    }
    
    public boolean isRoot() {
        return root;
    }
}

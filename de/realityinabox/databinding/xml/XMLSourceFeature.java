package de.realityinabox.databinding.xml;

import de.realityinabox.databinding.sourcemodel.Package;
import de.realityinabox.databinding.sourcemodel.Property;
import de.realityinabox.databinding.sourcemodel.SourceFactory;
import de.realityinabox.databinding.types.Type;

public class XMLSourceFeature extends SourceFactory {

    public XMLSourceFeature() {
        SourceFactory.setFactory(this);
    }

    protected de.realityinabox.databinding.sourcemodel.Package _newPackage(String name) {
        return new XMLPackage(name);
    }

    protected Property _newProperty(Package pack, String name, Type type) {
        return new XMLProperty(pack, name, type);
    }
}

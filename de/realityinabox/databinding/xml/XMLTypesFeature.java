package de.realityinabox.databinding.xml;

import de.realityinabox.databinding.sourcemodel.Package;
import de.realityinabox.databinding.types.GroupType;
import de.realityinabox.databinding.types.KeyRefType;
import de.realityinabox.databinding.types.KeyType;
import de.realityinabox.databinding.types.ListType;
import de.realityinabox.databinding.types.Type;
import de.realityinabox.databinding.types.TypeFactory;

public class XMLTypesFeature extends TypeFactory {

    public XMLTypesFeature() {
        TypeFactory.setFactory(this);
    }

    protected GroupType _newGroupType(Package pack, String name) {
        return new XMLGroupType(pack, name);
    }

    protected ListType _newListType(Package pack, String name) {
        return new XMLListType(pack, name);
    }

    protected KeyRefType _newKeyRefType(Type actualType, String keyName) {
        return new XMLKeyRefType(actualType, keyName);
    }

    protected KeyType _newKeyType(Type actualType, String keyName) {
        return new XMLKeyType(actualType, keyName);
    }
}

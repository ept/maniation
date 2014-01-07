package de.realityinabox.databinding.types;

import de.realityinabox.databinding.sourcemodel.ClassType;
import de.realityinabox.databinding.sourcemodel.Package;
import de.realityinabox.util.Pair;

public class GroupType extends CompositeType {

    protected GroupType(Package pack, String name) {
        super(pack, name);
    }

    public void makeProperties(ClassType container) {
        for (Pair<String,Type> c : getComponents()) {
            Type t = c.getRight();
            t.makeProperties(c.getLeft(), container);
        }
    }
}

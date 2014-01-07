package de.realityinabox.databinding.types;

import de.realityinabox.databinding.sourcemodel.ClassType;
import de.realityinabox.databinding.sourcemodel.MultiProperty;
import de.realityinabox.databinding.sourcemodel.Package;
import de.realityinabox.databinding.sourcemodel.SourceFactory;
import de.realityinabox.util.Pair;

public class ListType extends CompositeType {

    private boolean ordered = true;

    protected ListType(Package pack, String name) {
        super(pack, name);
    }
    
    public boolean isOrdered() {
        return ordered;
    }
    
    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public void makeProperties(ClassType container) {
        for (Pair<String,Type> c : getComponents()) {
            MultiProperty p = SourceFactory.newMultiProperty(getPackage(),
                    c.getLeft(), c.getRight());
            p.setOrdered(isOrdered());
            container.addProperty(p);
        }
    }

	public void addComponent(String name, Type component) {
		component.getClassType();
		super.addComponent(name, component);
	}
}

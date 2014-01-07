package de.realityinabox.databinding.types;

import de.realityinabox.databinding.sourcemodel.Package;
import de.realityinabox.databinding.sourcemodel.ClassType;
import de.realityinabox.databinding.sourcemodel.SourceFactory;

public abstract class PrimitiveType extends Type {

    protected PrimitiveType(Package pack) {
        super(pack);
    }
    
    public abstract String getJavaWrapper();

    public ClassType getClassType() {
        String name = getJavaName() + "-wrapper";
        name = getPackage().getUnusedClassName(name);
        ClassType wrapper = SourceFactory.newInterfaceImplPair(getPackage(), this, name);
        getPackage().addClass(wrapper);
        wrapper.addProperty(SourceFactory.newProperty(getPackage(), "value", this));
        return wrapper;
    }
}

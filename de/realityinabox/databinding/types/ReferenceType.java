package de.realityinabox.databinding.types;

import de.realityinabox.databinding.sourcemodel.Package;
import de.realityinabox.databinding.sourcemodel.ClassType;
import de.realityinabox.databinding.sourcemodel.SourceFactory;

public class ReferenceType extends Type {

    private String className;
    private String generatedName;
    private ClassType classType;

    protected ReferenceType(Package pack, String className, String generatedName) {
        super(pack);
        this.className = className;
        this.generatedName = generatedName;
        this.classType = null;
    }

    protected ReferenceType(ClassType classType) {
        super(classType.getPackage());
        this.className = null;
        this.generatedName = null;
        this.classType = classType;
    }
    
    public boolean hasImplementation() {
        return (this.className == null) && (this.generatedName == null) &&
            (this.classType != null);
    }
    
    public boolean structurallyEquals(Type t) {
        if (t instanceof ReferenceType) {
            ReferenceType rt = (ReferenceType) t;
            return ((this.className == null) && (rt.className == null) &&
                    (this.classType == rt.classType)) ||
                   ((this.className != null) && this.className.equals(rt.className));
        }
        return false;
    }

    public ClassType getClassType() {
        if (classType == null) {
            String name = getPackage().getUnusedClassName(generatedName);
            classType = SourceFactory.newInterfaceImplPair(getPackage(), this, name);
            getPackage().addClass(classType);
            ReferenceType wrapped = TypeFactory.newReferenceType(getPackage(), className, generatedName);
            classType.addProperty(SourceFactory.newProperty(getPackage(), "value", wrapped));
        }
        return classType;
    }
    
    public String getJavaName() {
        String name = null;
        if (classType != null) name = classType.getFullName();
        if (name != null) return name;
        return this.className;
    }
}

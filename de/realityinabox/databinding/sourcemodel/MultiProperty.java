package de.realityinabox.databinding.sourcemodel;

import de.realityinabox.databinding.types.Type;
import de.realityinabox.databinding.types.ReferenceType;
import de.realityinabox.databinding.types.PrimitiveType;
import de.realityinabox.databinding.types.GenericType;
import de.realityinabox.databinding.types.TypeFactory;

public class MultiProperty extends Property {

    private boolean ordered = true;

    protected MultiProperty(Package pack, String name, Type type) {
        super(pack, name, type);
        setAccessPermissions(Property.AccessPermissions.READ_ONLY);
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean isOrdered) {
        this.ordered = isOrdered;
    }

    protected void updateMethods() {
        String baseName = Tools.singularToPlural(getName());
        String fieldName = Tools.toJavaName(baseName, false);
        ReferenceType singleType;
        // Type of property must be either reference or primitive
        // (in the latter case the wrapper class is used)
        try {
            singleType = (ReferenceType) getType();
        } catch (ClassCastException e1) {
            try {
                String wrapper = ((PrimitiveType) getType()).getJavaWrapper();
                singleType = TypeFactory.newReferenceType(getPackage(), wrapper, wrapper);
            } catch (ClassCastException e2) {
                assert(false); return;
            }
        }
        GenericType multiType;
        if (isOrdered()) 
            multiType = TypeFactory.newGenericType(getPackage(), "java.util.List", "List"); else
            multiType = TypeFactory.newGenericType(getPackage(), "java.util.Collection", "Collection");
        multiType.addTypeArgument(TypeFactory.newTypeArgument(singleType));
        // Now we've got the property type.
        getFields().clear();
        getMethods().clear();
        Field field = SourceFactory.newField(fieldName, multiType);
        field.setAccessLevel(getFieldAccessLevel());
        field.setDefaultExpression("new java.util.ArrayList<" + singleType.getJavaName() + ">()");
        getFields().add(field);

        if ((getAccessPermissions() == Property.AccessPermissions.READ_ONLY) ||
            (getAccessPermissions() == Property.AccessPermissions.READ_WRITE)) {
            // Generate accessor method
            String mname = "get" + Tools.toJavaName(baseName, true);
            Method accessor = SourceFactory.newMethod(mname, multiType);
            accessor.setAccessLevel(getAccessLevel());
            accessor.addSourceLine("return " + fieldName + ";");
            getMethods().add(accessor);
        }
        // Never generate mutator method, even if requested -- it would violate
        // encapulation!
    }
}

package de.realityinabox.databinding.xml;

import java.util.Collection;
import java.util.Iterator;

import de.realityinabox.databinding.sourcemodel.AccessLevel;
import de.realityinabox.databinding.sourcemodel.Field;
import de.realityinabox.databinding.sourcemodel.Method;
import de.realityinabox.databinding.sourcemodel.Package;
import de.realityinabox.databinding.sourcemodel.Property;
import de.realityinabox.databinding.sourcemodel.SourceFactory;
import de.realityinabox.databinding.sourcemodel.Tools;
import de.realityinabox.databinding.types.KeyRefType;
import de.realityinabox.databinding.types.Type;
import de.realityinabox.databinding.types.TypeFactory;

public class XMLProperty extends Property {
    
    private Field keyRefField, upToDateField;
    private Method accessorMethod = null;
    private KeyRefType refType;

    public XMLProperty(Package pack, String name, Type type) {
        super(pack, name, type);
        if (type instanceof KeyRefType) {
            refType = (KeyRefType) type;
            keyRefField = SourceFactory.newField(Tools.toJavaName(name, false) + "KeyRef",
                    refType.getActualType());
            keyRefField.setAccessLevel(AccessLevel.PRIVATE);
            upToDateField = SourceFactory.newField(Tools.toJavaName(name, false) + "UpToDate",
                    TypeFactory.newBooleanType(pack));
            upToDateField.setAccessLevel(AccessLevel.PRIVATE);
            upToDateField.setDefaultExpression("false");
        } else {
            keyRefField = null;
            upToDateField = null;
        }
    }

    public Collection<Field> getFields() {
        Collection<Field> fields = super.getFields();
        if ((keyRefField != null) && (!fields.contains(keyRefField))) {
            fields.add(keyRefField);
            fields.add(upToDateField);
        }
        return fields;
    }

    public Collection<Method> getMethods() {
        Collection<Method> methods = super.getMethods();
        if (keyRefField == null) return methods;
        Method getter = null;
        Iterator<Method> it = methods.iterator();
        while (it.hasNext()) {
            Method m = it.next();
            if (m == accessorMethod) return methods;
            if (m.getReturnType().structurallyEquals(getType()) &&
                    (m.getArguments().size() == 0)) {
                getter = m;
                it.remove();
            }
        }
        if (getter == null) return methods;
        accessorMethod = SourceFactory.newMethod(getter.getName(), getter.getReturnType());
        accessorMethod.setAccessLevel(getter.getAccessLevel());
        accessorMethod.addSourceLine("if (!" + upToDateField.getName() + ") " + 
                Tools.toJavaName(getName(), false) + " = _document.map" +
                Tools.toJavaName(refType.getKeyName(), true) + "Keys.get(" +
                keyRefField.getName() + ");");
        accessorMethod.addSourceLine(upToDateField.getName() + " = true;");
        accessorMethod.addSourceLine("return " + Tools.toJavaName(getName(), false) + ";");
        methods.add(accessorMethod);
        return methods;
    }
    
    String generateMutatorCode(String source) {
        if (keyRefField == null) return null;
        return keyRefField.getName() + " = " + getType().javaConvertFromString(source) +
            "; " + upToDateField.getName() + " = false;";
    }
}

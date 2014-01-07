package de.realityinabox.databinding.sourcemodel;

import java.util.Collection;
import java.util.ArrayList;
import de.realityinabox.databinding.types.Type;
import de.realityinabox.databinding.types.TypeFactory;

public class Property {

    public enum AccessPermissions { READ_ONLY, WRITE_ONLY, READ_WRITE };

    private Package pack;
    private String name;
    private Type type;
    private AccessPermissions accessPermissions;
    private AccessLevel accessLevel;
    private AccessLevel fieldAccessLevel;
    private Collection<Field> fields;
    private Collection<Method> methods;
    private String getMethodPrefix;
    private String setMethodPrefix;

    protected Property(Package pack, String name, Type type) {
        this.pack = pack;
        this.name = name;
        this.type = type;
        // Put the following initializers here to force order of evaluation --
        // must be valid before updateMethods() is called!
        this.accessPermissions = AccessPermissions.READ_WRITE;
        this.accessLevel = AccessLevel.PUBLIC;
        this.fieldAccessLevel = AccessLevel.PRIVATE;
        this.fields = new ArrayList<Field>();
        this.methods = new ArrayList<Method>();
        this.getMethodPrefix = "get";
        this.setMethodPrefix = "set";
        updateMethods();
    }

    public Package getPackage() {
        return pack;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateMethods();
    }

    public Type getType() {
        return type;
    }

    public AccessPermissions getAccessPermissions() {
        return accessPermissions;
    }

    public void setAccessPermissions(AccessPermissions accessPermissions) {
        this.accessPermissions = accessPermissions;
        updateMethods();
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }
    
    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
        updateMethods();
    }

    public AccessLevel getFieldAccessLevel() {
        return fieldAccessLevel;
    }

    public void setFieldAccessLevel(AccessLevel fieldAccessLevel) {
        this.fieldAccessLevel = fieldAccessLevel;
        updateMethods();
    }

    public Collection<Field> getFields() {
        return fields;
    }

    public Collection<Method> getMethods() {
        return methods;
    }
    
    public String getGetMethodPrefix() {
        return getMethodPrefix;
    }

    public void setGetMethodPrefix(String getMethodPrefix) {
        this.getMethodPrefix = getMethodPrefix;
        updateMethods();
    }

    public String getSetMethodPrefix() {
        return setMethodPrefix;
    }

    public void setSetMethodPrefix(String setMethodPrefix) {
        this.setMethodPrefix = setMethodPrefix;
        updateMethods();
    }

    protected void updateMethods() {
        fields.clear();
        methods.clear();
        String fieldName = Tools.toJavaName(name, false);
        Field field = SourceFactory.newField(fieldName, getType());
        field.setAccessLevel(getFieldAccessLevel());
        fields.add(field);
        
        if ((getAccessPermissions() == AccessPermissions.READ_ONLY) ||
            (getAccessPermissions() == AccessPermissions.READ_WRITE)) {
            // Generate accessor method
            String mname = getGetMethodPrefix() + Tools.toJavaName(getName(), true);
            Method accessor = SourceFactory.newMethod(mname, getType());
            accessor.setAccessLevel(getAccessLevel());
            accessor.addSourceLine("return " + fieldName + ";");
            methods.add(accessor);
        }
        
        if ((getAccessPermissions() == AccessPermissions.WRITE_ONLY) ||
            (getAccessPermissions() == AccessPermissions.READ_WRITE)) {
            // Generate mutator method
            String mname = getSetMethodPrefix() + Tools.toJavaName(getName(), true);
            Method mutator = SourceFactory.newMethod(mname, TypeFactory.newVoidType(getPackage()));
            mutator.setAccessLevel(getAccessLevel());
            mutator.addArgument(SourceFactory.newVariable(fieldName, getType()));
            String validator = getType().javaValidation(fieldName);
            if (!validator.equals("")) mutator.addSourceLine(validator);
            mutator.addSourceLine("this." + fieldName + " = " + fieldName + ";");
            methods.add(mutator);
        }
    }
}

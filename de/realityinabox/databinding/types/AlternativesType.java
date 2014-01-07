package de.realityinabox.databinding.types;

import de.realityinabox.databinding.sourcemodel.AccessLevel;
import de.realityinabox.databinding.sourcemodel.ClassType;
import de.realityinabox.databinding.sourcemodel.Enum;
import de.realityinabox.databinding.sourcemodel.Package;
import de.realityinabox.databinding.sourcemodel.SourceFactory;
import de.realityinabox.databinding.sourcemodel.Tools;
import de.realityinabox.util.Pair;

public class AlternativesType extends CompositeType {
    
    private ClassType enclosingClass = null;
    private ClassType generatedClass = null;

    protected AlternativesType(Package pack, String name) {
        super(pack, name);
    }
    
    private void generateClass() {
        if (generatedClass != null) return;
        if (getComponents().size() == 2) {
            BooleanType b = TypeFactory.newBooleanType(getPackage());
            generatedClass = b.getClassType();
        } else {
            String clsname = getName();
            if (getCommonType() != null) clsname += "-repr";
            clsname = getPackage().getUnusedClassName(clsname);
            Enum e;
            if (!isGlobal() && (enclosingClass != null)) {
                e = SourceFactory.newEnum(getPackage(), this, enclosingClass, clsname);
                enclosingClass.addNestedClass(e);
            } else {
                e = SourceFactory.newEnum(getPackage(), this, clsname);
                getPackage().addClass(e);
            }
            e.setAccessLevel(AccessLevel.PUBLIC);
            for (Pair<String,Type> c : getComponents()) {
                String name = Tools.toJavaConstant(c.getLeft());
                e.addConstant(SourceFactory.newEnumConstant(name));
            }
            generatedClass = e;
        }
    }
    
    private Type getCommonType() {
        Type firstType = null;
        boolean typesEqual = true;
        for (Pair<String,Type> c : getComponents()) {
            if (firstType == null) firstType = c.getRight(); else
                if (!firstType.structurallyEquals(c.getRight())) typesEqual = false;
        }
        if (typesEqual && !(firstType instanceof VoidType)) return firstType; 
        return null;
    }

    public ClassType getClassType() {
        generateClass();
        return generatedClass;
    }

    public String getJavaName() {
        if (getComponents().size() == 2) return "boolean";
        generateClass();
        return generatedClass.getFullName();
    }
    
    private void addRepresentationProperty(ClassType container, String sourceName) {
        Type ptype; 
        String pname = getName();
        if (getComponents().size() == 2) {
            ptype = TypeFactory.newBooleanType(getPackage());
            Representation stronger = Representation.getStronger(
                    getComponents().get(0).getRight().getRepresentation(),
                    getComponents().get(1).getRight().getRepresentation());
            for (Pair<String,Type> pair : getComponents()) {
                if (pair.getRight().getRepresentation() == stronger) {
                    pname += "-" + stronger.getRepresentationName(pair.getLeft());
                    break;
                }
            }
        } else ptype = TypeFactory.newReferenceType(getClassType());
        ptype.makeProperties(pname + "-" + sourceName, container);
    }

    public void makeProperties(ClassType container) {
        enclosingClass = container;
        Type commonType = getCommonType();
        if (commonType != null) {
            commonType.makeProperties(getName(), container);
            addRepresentationProperty(container, "repr");
        } else {
            for (Pair<String,Type> c : getComponents()) {
                Type t = c.getRight();
                if (t instanceof VoidType) continue;
                t.makeProperties(c.getLeft(), container);
            }
            addRepresentationProperty(container, "");
        }
    }
}

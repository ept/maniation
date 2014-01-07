package de.realityinabox.databinding.types;

import java.util.Iterator;
import java.util.List;
import de.realityinabox.util.Pair;
import de.realityinabox.databinding.sourcemodel.Package;
import de.realityinabox.databinding.sourcemodel.InterfaceImplPair;
import de.realityinabox.databinding.sourcemodel.ClassType;
import de.realityinabox.databinding.sourcemodel.SourceFactory;

public abstract class CompositeType extends ReferenceType {

    private Package pack;
    private String name;
    private List<Pair<String,Type>> components = new java.util.ArrayList<Pair<String,Type>>();
    private InterfaceImplPair generatedPair = null;

    protected CompositeType(Package pack, String name) {
        super(pack, "", name);
        this.pack = pack;
        this.name = name;
    }

    public Package getPackage() {
        return pack;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Pair<String,Type>> getComponents() {
        return components;
    }

    public void addComponent(String componentName, Type component) {
        components.add(new Pair<String,Type>(componentName, component));
    }
    
    public boolean structurallyEquals(Type t) {
        if (t instanceof CompositeType) {
            List<Pair<String,Type>> entries = new java.util.LinkedList<Pair<String,Type>>();
            entries.addAll(((CompositeType) t).getComponents());
            for (Pair<String,Type> pair : getComponents()) {
                Iterator<Pair<String,Type>> it = entries.iterator();
                boolean found = false;
                while (it.hasNext()) {
                    if (it.next().getRight().structurallyEquals(pair.getRight())) {
                        it.remove();
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }
            return entries.size() == 0;
        }
        return false;
    }

    public ClassType getClassType() {
        generateClass();
        return generatedPair;
    }

    public String getJavaName() {
        generateClass();
        return generatedPair.getFullName();
    }

    public void makeProperties(String unusedName, ClassType container) {
        makeProperties(container);
    }
    
    public abstract void makeProperties(ClassType container);

    private void generateClass() {
    	if (generatedPair != null) return;
        String clsname = getPackage().getUnusedClassName(getName());
        generatedPair = SourceFactory.newInterfaceImplPair(getPackage(), this, clsname);
        makeProperties(generatedPair);
        getPackage().addClass(generatedPair);
    }
}

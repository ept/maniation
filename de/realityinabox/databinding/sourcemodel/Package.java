package de.realityinabox.databinding.sourcemodel;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.FileOutputStream;
import de.realityinabox.databinding.types.KeyRefType;
import de.realityinabox.databinding.types.KeyType;
import de.realityinabox.databinding.types.Type;
import de.realityinabox.databinding.types.TypeFactory;

public class Package {
    
    private String name;
    private Map<String,ClassType> classes = new HashMap<String,ClassType>();
    private Map<String, KeyRefType> keyAttributeMapping = new HashMap<String,KeyRefType>();

    protected Package(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public Collection<ClassType> getClasses() {
        ArrayList<ClassType> result = new ArrayList<ClassType>();
        result.addAll(classes.values());
        return result;
    }

    public void addClass(ClassType cls) {
        assert(!classes.containsKey(cls.getName()));
        classes.put(cls.getName(), cls);
        for (Property p : cls.getProperties()) {
            Type pt = p.getType();
            if (pt instanceof KeyType) {
                KeyType k = (KeyType) pt;
                KeyRefType kr = getKeyMapping(k.getActualType(), k.getKeyName());
                kr.setClassType(cls, p);
            }
        }
        System.out.println("added class " + cls.getName());
    }

    public String getUnusedClassName(String basis) {
        String adjusted = Tools.toJavaName(basis, true);
        String result = adjusted;
        int i = 0;
        do {
            result = adjusted + ( i > 0 ? i + "" : "" );
            i++;
        } while (classes.containsKey(result) || classes.containsKey(result + "Impl"));
        return result;
    }
    
    public KeyRefType getKeyMapping(Type actualType, String keyRef) {
        KeyRefType destination = keyAttributeMapping.get(keyRef);
        if (destination == null) {
            destination = TypeFactory.newKeyRefType(actualType, keyRef);
            keyAttributeMapping.put(keyRef, destination);
        }
        return destination;
    }

    public void write(SourceContext context) throws java.io.IOException {
        String dir = name.replace('.', '/');
        (new File(dir)).mkdirs();
        System.out.println("***started writing");
        for (ClassType c : classes.values()) {
            FileOutputStream stream = new FileOutputStream(dir + "/" + c.getName() + ".java");
            context.println(stream, "package " + name + ";");
            context.println(stream, "");
            c.write(stream, context.duplicate());
            stream.close();
        }
    }
}

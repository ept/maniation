package de.kleppmann.maniation;

import de.kleppmann.maniation.scene.Scene;
import de.kleppmann.maniation.scene.XMLParser;
import de.realityinabox.databinding.libs.XMLWriter;

public class Maniation {
    
    private Scene scene;
    
    public Maniation() {
        XMLParser sceneParser = new XMLParser(new org.apache.xerces.jaxp.SAXParserFactoryImpl());
        try {
            scene = sceneParser.parse("../alfred.xml");
            new XMLWriter(scene, System.out);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static void main(String[] args) {
        new Maniation();
    }
}

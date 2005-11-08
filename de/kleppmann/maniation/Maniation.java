package de.kleppmann.maniation;

import de.kleppmann.maniation.scene.Scene;
import de.kleppmann.maniation.scene.XMLParser;

public class Maniation {
    
    SceneWindow sceneWindow;
    
    public Maniation() {
        XMLParser sceneParser = new XMLParser(new org.apache.xerces.jaxp.SAXParserFactoryImpl());
        Scene scene;
        try {
            scene = sceneParser.parse("../alfred.xml");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        sceneWindow = new SceneWindow(scene);
    }

    public static void main(String[] args) {
        new Maniation();
    }
}

package de.kleppmann.maniation;

import de.kleppmann.maniation.dynamics.DynamicScene;
import de.kleppmann.maniation.dynamics.Simulation;
import de.kleppmann.maniation.scene.Scene;
import de.kleppmann.maniation.scene.XMLParser;

public class TestDynamics {

    public TestDynamics() {
        XMLParser sceneParser = new XMLParser(new org.apache.xerces.jaxp.SAXParserFactoryImpl());
        Scene scene;
        try {
            scene = sceneParser.parse("../boxes.xml");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Simulation sim = new Simulation();
        //MultiPendulum.setup(sim, 8);
        //new NewtonsCradle(sim);
        //sim.addBody(new Gyroscope(sim.getWorld()));
        new DynamicScene(scene, sim);
        sim.run(6.0);
    }

    public static void main(String[] args) {
        new TestDynamics();
    }
}

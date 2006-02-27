package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.geometry.AnimateMesh;
import de.kleppmann.maniation.scene.Body;
import de.kleppmann.maniation.scene.Scene;

public class DynamicScene {
    
    public DynamicScene(Scene scene, Simulation sim) {
        for (Body body : scene.getBodies()) {
            AnimateMesh geometry = new AnimateMesh(body);
            MeshBody dynamics = MeshBody.newMeshBody(sim.getWorld(), geometry);
            geometry.setDynamicBody(dynamics);
            sim.addBody(dynamics);
        }
    }
}

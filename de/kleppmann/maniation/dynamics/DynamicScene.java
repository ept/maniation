package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.geometry.AnimateMesh;
import de.kleppmann.maniation.geometry.ArticulatedMesh;
import de.kleppmann.maniation.scene.Body;
import de.kleppmann.maniation.scene.Scene;

public class DynamicScene {
    
    public DynamicScene(Scene scene, Simulation sim) {
        for (Body body : scene.getBodies()) {
            if (body.getMesh().getSkeleton() == null)
                sim.addBody(new MeshBody(sim.getWorld(), new AnimateMesh(body)));
            else sim.addBody(new ArticulatedBody(sim.getWorld(), new ArticulatedMesh(body)));
        }
    }
}

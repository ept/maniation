package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.scene.Mesh;

public class ArticulatedBody extends CompoundBody {

    public ArticulatedBody(Mesh mesh) {
        super(bodiesFromMesh(mesh));
        // TODO Auto-generated constructor stub
    }
    
    private static Body[] bodiesFromMesh(Mesh mesh) {
        Body[] result = new Body[mesh.getSkeleton().getBones().size()];
        return result;
    }
    
}

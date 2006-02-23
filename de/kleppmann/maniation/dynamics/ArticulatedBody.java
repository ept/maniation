package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.scene.Mesh;

public class ArticulatedBody extends CompoundBody {

    public ArticulatedBody(Mesh mesh) {
        super(bodiesFromMesh(mesh));
        // TODO Auto-generated constructor stub
    }
    
    private static GeneralizedBody[] bodiesFromMesh(Mesh mesh) {
        GeneralizedBody[] result = new GeneralizedBody[mesh.getSkeleton().getBones().size()];
        return result;
    }
    
}

package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.scene.Mesh;

public class ArticulatedBody extends CompoundBody implements Body {

    public ArticulatedBody(Mesh mesh) {
        super(bodiesFromMesh(mesh));
        // TODO Auto-generated constructor stub
    }
    
    private static GeneralizedBody[] bodiesFromMesh(Mesh mesh) {
        GeneralizedBody[] result = new GeneralizedBody[mesh.getSkeleton().getBones().size()];
        return result;
    }

    public Vector3D getLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    public Quaternion getOrientation() {
        // TODO Auto-generated method stub
        return null;
    }
    
}

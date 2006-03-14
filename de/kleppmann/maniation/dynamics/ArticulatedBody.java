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

    public State getInitialState() {
        // TODO Auto-generated method stub
        return null;
    }
    
    private class State extends StateVector implements Body.State {
        State() {
            super(ArticulatedBody.this, null);
        }

        public ArticulatedBody getOwner() {
            return ArticulatedBody.this;
        }

        public Vector3D getCoMPosition() {
            // TODO Auto-generated method stub
            return null;
        }

        public Quaternion getOrientation() {
            // TODO Auto-generated method stub
            return null;
        }
        
        public Vector3D getCoMVelocity() {
            // TODO Auto-generated method stub
            return null;
        }

        public Vector3D getAngularVelocity() {
            // TODO Auto-generated method stub
            return null;
        }
    }
}

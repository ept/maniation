package de.kleppmann.maniation.geometry;

import java.util.Set;

import de.kleppmann.maniation.dynamics.Body;
import de.kleppmann.maniation.dynamics.GeneralizedBody;
import de.kleppmann.maniation.dynamics.MeshBody;
import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.scene.Bone;

public class ArticulatedLimb extends AnimateMesh {
    
    private MeshTriangle[] triangles;
    private ArticulatedLimb parent;
    private CollisionVolume volume;
    private MeshBody dynamicBody;
    private Body.State dynamicState;
    private Vector3D baseRest, baseCurrent;
    private Quaternion orientRest, orientCurrent;
    
    public ArticulatedLimb(Set<MeshTriangle> triangles, Bone bone, ArticulatedLimb parent,
            ArticulatedMesh wholeMesh) {
        super(null);
        this.triangles = triangles.toArray(new MeshTriangle[triangles.size()]);
        this.parent = parent;
        this.volume = new CollisionVolume(this.triangles);
        // Determine rest position and orientation
        if (parent != null) {
            baseRest = parent.baseRest; orientRest = parent.orientRest;
        } else {
            baseRest = wholeMesh.getLocation(); orientRest = wholeMesh.getOrientation();
        }
        Vector3D local = new Vector3D(bone.getBase().getX(), bone.getBase().getY(), bone.getBase().getZ());
        baseRest = baseRest.add(orientRest.transform(local));
        orientRest = orientRest.mult(bone.getOrientation().getValue());
        baseCurrent = baseRest; orientCurrent = orientRest;
    }
    
    public Vector3D currentVertexPosition(Vector3D pos) {
        Vector3D local = orientRest.getInverse().transform(pos.subtract(baseRest));
        return orientCurrent.transform(local).add(baseCurrent);
    }

    @Override
    public MeshBody getDynamicBody() {
        return dynamicBody;
    }
    
    @Override
    public void setDynamicBody(GeneralizedBody dynamicBody) {
        this.dynamicBody = (MeshBody) dynamicBody;
        for (MeshTriangle tri : triangles) tri.setBody(this.dynamicBody);
    }
    
    @Override
    public Body.State getDynamicState() {
        return dynamicState;
    }
    
    @Override
    public void setDynamicState(GeneralizedBody.State state, Vector3D com) {
        if (!(state instanceof Body.State)) throw new IllegalArgumentException();
        com = dynamicBody.getCentreOfMass();
        this.dynamicState = (Body.State) state;
        this.baseCurrent = dynamicState.getCoMPosition().subtract(
                dynamicState.getOrientation().transform(com));
        this.orientCurrent = dynamicState.getOrientation();
    }
    
    @Override
    public Vector3D getLocation() {
        return baseRest;
    }

    @Override
    public Quaternion getOrientation() {
        return orientRest;
    }

    @Override
    public CollisionVolume getCollisionVolume() {
        return volume;
    }
    
    @Override
    public MeshTriangle[] getTriangles() {
        return triangles;
    }

    public ArticulatedLimb getParent() {
        return parent;
    }
    
    @Override
    public String toString() {
        return orientCurrent.getW() + " " + orientCurrent.getX() + " " + 
                orientCurrent.getY() + " " + orientCurrent.getZ();
    }
}

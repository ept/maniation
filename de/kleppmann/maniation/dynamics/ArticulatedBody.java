package de.kleppmann.maniation.dynamics;

import java.util.Map;
import java.util.Set;

import de.kleppmann.maniation.geometry.AnimateMesh;
import de.kleppmann.maniation.geometry.ArticulatedLimb;
import de.kleppmann.maniation.geometry.ArticulatedMesh;
import de.kleppmann.maniation.geometry.MeshVertex;
import de.kleppmann.maniation.scene.Bone;
import de.kleppmann.maniation.scene.Skeleton;

public class ArticulatedBody extends CompoundBody implements Collideable {

    private World world;
    private ArticulatedMesh mesh;
    private Map<ArticulatedLimb, Set<Integer>> collisionTestLimbs;

    public ArticulatedBody(World world, ArticulatedMesh mesh) {
        super(world, bodiesFromMesh(world, mesh));
        this.world = world;
        this.mesh = mesh;
        mesh.setDynamicBody(this);
        // Determine which bones should be tested for collision against each other.
        // Tests should not be symmetric (if A is tested against B, B should not be tested
        // against A), and any two bones whose triangle sets share a vertex (i.e. they are
        // directly adjacent parts of the mesh) should not be tested against each other.
        Skeleton skel = mesh.getSceneBody().getMesh().getSkeleton();
        collisionTestLimbs = new java.util.HashMap<ArticulatedLimb, Set<Integer>>();
        for (int i=0; i < skel.getBones().size(); i++) {
            Bone bone = skel.getBones().get(i);
            Set<Integer> limbSet = new java.util.HashSet<Integer>();
            for (int j=0; j<i; j++) {
                Bone other = skel.getBones().get(j);
                Set<MeshVertex> intersect = new java.util.HashSet<MeshVertex>();
                intersect.addAll(mesh.getBoneVerticesMap().get(bone));
                intersect.retainAll(mesh.getBoneVerticesMap().get(other));
                if (intersect.size() == 0) {
                    ArticulatedLimb l = mesh.getBoneLimbMap().get(other);
                    for (int k=0; k<getBodies(); k++) if (l == mesh.getLimbList()[i]) limbSet.add(k);
                }
            }
            collisionTestLimbs.put(mesh.getBoneLimbMap().get(bone), limbSet);
        }
        
    }
    
    private static GeneralizedBody[] bodiesFromMesh(World world, ArticulatedMesh mesh) {
        GeneralizedBody[] result = new GeneralizedBody[mesh.getLimbList().length];
        for (int i=0; i<result.length; i++) result[i] = new MeshBody(world, mesh.getLimbList()[i]);
        return result;
    }

    @Override
    public MeshBody getBody(int index) {
        return (MeshBody) super.getBody(index);
    }

    @Override
    public StateVector getInitialState() {
        return new StateVector(this, getBodyArray());
    }

    @Override
    public void interaction(SimulationObject.State ownState, SimulationObject.State partnerState,
            InteractionList result, boolean allowReverse) {
        if (!(ownState instanceof StateVector)) throw new IllegalArgumentException();
        StateVector me = (StateVector) ownState;
        if (me.getOwner() != this) throw new IllegalArgumentException();
        mesh.setDynamicState(me, null);
        for (int i=0; i<getBodies(); i++) {
            try {
                MeshBody body = (MeshBody) getBody(i);
                Body.State bstate = (Body.State) me.getSlice(i);
                ArticulatedLimb limb = (ArticulatedLimb) body.getMesh();
                // If interaction partner supports collision detection, check for collision.
                if (partnerState.getOwner() instanceof Collideable) {
                    Collideable partner = (Collideable) partnerState.getOwner();
                    partner.collide((GeneralizedBody.State) partnerState, limb, result);
                } else body.interaction(bstate, partnerState, result, true);
                // If interacting with the world, we also test for collision amongst the limbs
                if (partnerState.getOwner() == world) {
                    for (Integer testLimb : collisionTestLimbs.get(limb))
                        body.interaction(bstate, me.getSlice(testLimb), result, true);
                }
            } catch (ClassCastException e) {
                throw new IllegalStateException(e);
            }
        }
    }
    
    public void collide(GeneralizedBody.State ownState, AnimateMesh partner, InteractionList result) {
        if (!(ownState instanceof StateVector)) throw new IllegalArgumentException();
        StateVector me = (StateVector) ownState;
        if (me.getOwner() != this) throw new IllegalArgumentException();
        try {
            for (int i=0; i<getBodies(); i++) {
                MeshBody body = (MeshBody) getBody(i);
                Body.State state = (Body.State) me.getSlice(i);
                body.collide(state, partner, result);
            }
        } catch (ClassCastException e) {
            throw new IllegalStateException(e);
        }
    }
}

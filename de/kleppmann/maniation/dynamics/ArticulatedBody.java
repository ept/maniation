package de.kleppmann.maniation.dynamics;

import java.util.Map;
import java.util.Set;

import de.kleppmann.maniation.geometry.AnimateMesh;
import de.kleppmann.maniation.geometry.ArticulatedLimb;
import de.kleppmann.maniation.geometry.ArticulatedMesh;
import de.kleppmann.maniation.geometry.MeshVertex;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.scene.AxisConstraint;
import de.kleppmann.maniation.scene.Bone;
import de.kleppmann.maniation.scene.Skeleton;

public class ArticulatedBody extends CompoundBody implements Collideable {

    private World world;
    private ArticulatedMesh mesh;
    private Map<ArticulatedLimb, Set<Integer>> collisionTestLimbs;
    private Map<SimulationObject, Set<Interaction>> links;

    public ArticulatedBody(World world, ArticulatedMesh mesh) {
        super(world, bodiesFromMesh(world, mesh));
        this.world = world;
        this.mesh = mesh;
        mesh.setDynamicBody(this);
        // Collect links between the limbs into a single map
        links = new java.util.HashMap<SimulationObject, Set<Interaction>>();
        for (int i=0; i<getBodies(); i++) {
            Limb limb = (Limb) getBody(i);
            links.put(limb, limb.links);
        }
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
        Limb[] result = new Limb[mesh.getLimbList().length];
        Map<ArticulatedLimb, Limb> bodyMap = new java.util.HashMap<ArticulatedLimb, Limb>();
        for (int i=0; i<result.length; i++) {
            ArticulatedLimb limb = mesh.getLimbList()[i];
            result[i] = new Limb(world, limb);
            bodyMap.put(limb, result[i]);
            if (limb.getParent() != null) {
                Limb parent = bodyMap.get(limb.getParent());
                if (parent == null) throw new IllegalStateException();
                Vector3D thispos = result[i].getInitialOrientation().getInverse().transform(
                        limb.getCurrentLocation().subtract(result[i].getInitialPosition()));
                Vector3D parentpos = parent.getInitialOrientation().getInverse().transform(
                        limb.getCurrentLocation().subtract(parent.getInitialPosition()));
                JointConstraint link = new JointConstraint(result[i], thispos, parent, parentpos);
                parent.links.add(link);
                result[i].links.add(link);
                makeRotationConstraint(limb.getBone().getXAxis(), new Vector3D(1,0,0), parent, result[i]);
                makeRotationConstraint(limb.getBone().getYAxis(), new Vector3D(0,1,0), parent, result[i]);
                makeRotationConstraint(limb.getBone().getZAxis(), new Vector3D(0,0,1), parent, result[i]);
            }
        }
        return result;
    }
    
    private static void makeRotationConstraint(AxisConstraint info, Vector3D axis, Limb parent, Limb child) {
        if (info == null) return;
        RotationConstraint constr = null;
        if (Math.abs(info.getMaxExtreme() - info.getMinExtreme()) < 1e-6) {
            constr = new RotationConstraint(null, child, axis, parent, 0);
            parent.links.add(constr); child.links.add(constr);
        } else {
            constr = new RotationConstraint(null, child, axis, parent, info.getMaxExtreme());
            parent.links.add(constr); child.links.add(constr);
            constr = new RotationConstraint(null, child, axis, parent, info.getMinExtreme());
            parent.links.add(constr); child.links.add(constr);
        }
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
                // and include the joint links
                if (partnerState.getOwner() == world) {
                    for (Integer testLimb : collisionTestLimbs.get(limb))
                        body.interaction(bstate, me.getSlice(testLimb), result, true);
                    for (Interaction link : links.get(body)) result.addInteraction(link);
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
    
    
    private static class Limb extends MeshBody {
        private Set<Interaction> links;
        
        public Limb(World world, AnimateMesh mesh) {
            super(world, mesh);
            links = new java.util.HashSet<Interaction>();
        }
    }
}

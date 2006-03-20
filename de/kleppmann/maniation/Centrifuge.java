package de.kleppmann.maniation;

import java.util.List;
import java.util.Map;

import de.kleppmann.maniation.dynamics.GeneralizedBody;
import de.kleppmann.maniation.dynamics.Cylinder;
import de.kleppmann.maniation.dynamics.Interaction;
import de.kleppmann.maniation.dynamics.InteractionForce;
import de.kleppmann.maniation.dynamics.InteractionList;
import de.kleppmann.maniation.dynamics.JointConstraint;
import de.kleppmann.maniation.dynamics.NailConstraint;
import de.kleppmann.maniation.dynamics.RotationConstraint;
import de.kleppmann.maniation.dynamics.Simulation;
import de.kleppmann.maniation.dynamics.SimulationObject;
import de.kleppmann.maniation.dynamics.World;
import de.kleppmann.maniation.dynamics.SimulationObject.State;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.maths.VectorImpl;

public class Centrifuge extends Cylinder {

    private boolean sphere;
    private Map<SimulationObject,Interaction[]> interactions;
    private static final double[] torque = {0.0, 0.0, 0.0, 0.0, 0.0, 0.01};
    private static final Vector torqueVector = new VectorImpl(torque);
    
    private Centrifuge(boolean sphere) {
        super(new Vector3D(0, 0, 1), 0.5, 1.0, 1.0);
        this.sphere = sphere;
    }

    @Override
    protected Vector3D getInitialPosition() {
        if (sphere) return new Vector3D(0, 0.7, 2.2);
        return new Vector3D(0, 0, 0);
    }

    @Override
    public void interaction(State ownState, State partnerState, InteractionList result, boolean allowReverse) {
        super.interaction(ownState, partnerState, result, allowReverse);
        Interaction[] ia = interactions.get(partnerState.getOwner());
        if (ia != null) for (Interaction i : ia) result.addInteraction(i);
    }


    public static void setup(Simulation sim) {
        Centrifuge cylinder = new Centrifuge(false);
        Centrifuge sphere = new Centrifuge(true);
        sim.addBody(cylinder); sim.addBody(sphere);
        cylinder.interactions = new java.util.HashMap<SimulationObject,Interaction[]>();
        sphere.interactions = new java.util.HashMap<SimulationObject,Interaction[]>();
        Interaction[] cyl = {
            new NailConstraint(sim.getWorld(), cylinder, new Vector3D(0, 0, 0), new Vector3D(0, 0, 0)),
            new RotationConstraint(sim.getWorld(), null, new Vector3D(1,0,0), cylinder, 0),
            new RotationConstraint(sim.getWorld(), null, new Vector3D(0,1,0), cylinder, 0),
            new AcceleratingTorque(sim.getWorld(), cylinder)
        };
        cylinder.interactions.put(sim.getWorld(), cyl);
        Interaction[] sph = {
            new JointConstraint(cylinder, new Vector3D(0, 0.7, 3), sphere, new Vector3D(0, 0, 0.8)),
            new RotationConstraint(sim.getWorld(), cylinder, new Vector3D(0,1,0), sphere, 0),
            new RotationConstraint(sim.getWorld(), cylinder, new Vector3D(0,0,1), sphere, 0)
        };
        cylinder.interactions.put(sphere, sph);
        sphere.interactions.put(cylinder, sph);
    }
    
    
    private static class AcceleratingTorque implements InteractionForce {
        private World world;
        private GeneralizedBody body;
        
        public AcceleratingTorque(World world, GeneralizedBody body) {
            this.world = world; this.body = body;
        }
        
        public Vector getForceTorque(GeneralizedBody b) {
            return torqueVector;
        }
        
        public List<SimulationObject> getObjects() {
            List<SimulationObject> result = new java.util.ArrayList<SimulationObject>();
            result.add(world); result.add(body);
            return result;
        }
    }
}

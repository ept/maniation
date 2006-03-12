package de.kleppmann.maniation.dynamics;

import java.util.List;

import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.maths.VectorImpl;

public class World implements SimulationObject {

    // Gravity: 9.81 N/kg along negative Z axis
    static final Vector GRAVITY = new Vector3D(0.0, 0.0, -9.81);

    public State getInitialState() {
        return new WorldState();
    }


    private class WorldState implements SimulationObject.State {
        public SimulationObject getOwner() {
            return World.this;
        }

        public State handleInteraction(State state, Interaction action) {
            return state;
        }

        public void interaction(State partnerState, InteractionList result, boolean allowReverse) {
            if (partnerState instanceof GeneralizedBody.State)
                result.addInteraction(new Gravity((GeneralizedBody.State) partnerState));
        }
    }
    
    private class Gravity implements InteractionForce {
        private GeneralizedBody.State bodyState;
        private Vector forceTorque;
        
        public Gravity(GeneralizedBody.State bodyState) {
            this.bodyState = bodyState;
            double[] gv = new double[bodyState.getVelocities().getDimension()];
            for (int i=0; i<gv.length; i++)
                gv[i] = (i % 6 < 3) ? GRAVITY.getComponent(i % 6) : 0.0;
            forceTorque = bodyState.getMassInertia().mult(new VectorImpl(gv));
        }

        public Vector getForceTorque(GeneralizedBody b) {
            if (b == bodyState.getOwner()) return forceTorque;
            return null;
        }

        public List<SimulationObject> getObjects() {
            List<SimulationObject> result = new java.util.ArrayList<SimulationObject>();
            result.add(World.this);
            result.add(bodyState.getOwner());
            return result;
        }
    }
}

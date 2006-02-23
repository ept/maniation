package de.kleppmann.maniation.dynamics;

import java.util.List;

import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.maths.VectorImpl;

public class World implements SimulationObject {

    // Gravity: 9.81 N/kg along negative Z axis
    static final Vector GRAVITY = new Vector3D(0.0, 0.0, -3.0 /*-9.81*/);

    public void handleInteraction(Interaction action) {}

    public void interaction(SimulationObject partner, InteractionList result, boolean allowReverse) {
        if (partner instanceof GeneralizedBody) result.addInteraction(new Gravity((GeneralizedBody) partner));
    }
    
    
    private class Gravity implements InteractionForce {
        private GeneralizedBody body;
        private Vector forceTorque;
        
        public Gravity(GeneralizedBody b) {
            this.body = b;
            double[] gv = new double[body.getVelocities().getDimension()];
            for (int i=0; i<gv.length; i++)
                gv[i] = (i % 6 < 3) ? GRAVITY.getComponent(i % 6) : 0.0;
            forceTorque = body.getMassInertia().mult(new VectorImpl(gv));
        }

        public Vector getForceTorque(GeneralizedBody b) {
            if (b == body) return forceTorque;
            return null;
        }

        public List<SimulationObject> getObjects() {
            List<SimulationObject> result = new java.util.ArrayList<SimulationObject>();
            result.add(World.this);
            result.add(body);
            return result;
        }
    }
}

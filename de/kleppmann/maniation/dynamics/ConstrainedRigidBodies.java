package de.kleppmann.maniation.dynamics;

import java.util.List;

import de.kleppmann.maniation.maths.ODE;
import de.kleppmann.maniation.maths.Vector;

public class ConstrainedRigidBodies implements ODE {

    private List<RigidBody> bodies = new java.util.ArrayList<RigidBody>();
    
    public ConstrainedRigidBodies() {
    }
    
    public void addBody(RigidBody body) {
        bodies.add(body);
    }

    public Vector getInitial() {
        StateVector init = new StateVector();
        init.setDerivative(false);
        for (RigidBody b : bodies) init.addBody(b);
        return init;
    }

    public Vector derivative(double time, Vector state) {
        if (!(state instanceof StateVector)) throw new IllegalArgumentException();
        StateVector s = (StateVector) state;
        s.updateBodies();
        return s.getDerivative();
    }
}

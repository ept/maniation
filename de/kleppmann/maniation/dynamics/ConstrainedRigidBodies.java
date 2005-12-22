package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.ODE;
import de.kleppmann.maniation.maths.Vector;

public class ConstrainedRigidBodies implements ODE {

    public ConstrainedRigidBodies() {
    }

    public Vector derivative(double time, Vector state) {
        if (!(state instanceof StateVector)) throw new IllegalArgumentException();
        StateVector s = (StateVector) state;
        s.updateBodies();
        return s.getDerivative();
    }
}

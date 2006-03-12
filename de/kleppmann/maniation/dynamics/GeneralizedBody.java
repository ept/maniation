package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.Vector;

public interface GeneralizedBody extends SimulationObject {
    
    public interface State extends SimulationObject.State, Vector {
        State getDerivative();
        Vector getVelocities();
        Vector getAccelerations();
        Matrix getMassInertia();
        State applyForce(Vector forceTorque);
        State applyImpulse(Vector impulse);
        double getEnergy();
        GeneralizedBody getOwner();
    }

    GeneralizedBody.State getInitialState();
}

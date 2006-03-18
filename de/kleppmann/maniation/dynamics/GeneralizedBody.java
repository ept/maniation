package de.kleppmann.maniation.dynamics;

import java.util.Map;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.Vector3D;

public interface GeneralizedBody extends SimulationObject {
    
    public interface State extends SimulationObject.State, Vector {
        State getDerivative();
        Vector getVelocities();
        Vector getAccelerations();
        Matrix getMassInertia();
        State applyForce(Vector forceTorque);
        State applyImpulse(Vector impulse);
        State applyPosition(Map<Body, Vector3D> newPositions);
        double getEnergy();
        GeneralizedBody getOwner();
        State mult(double scalar);
        State add(Vector v);
        State subtract(Vector v);
    }

    GeneralizedBody.State getInitialState();
}

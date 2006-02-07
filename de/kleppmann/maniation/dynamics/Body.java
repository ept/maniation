package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.Vector;

public interface Body extends SimulationObject {
    void setSimulationTime(double time);
    Vector getState(boolean rateOfChange);
    void setState(Vector state);
    Vector getVelocities();
    Vector getAccelerations();
    Matrix getMassInertia();
    void applyForce(Vector forceTorque);
    void applyImpulse(Vector impulse);
    double getEnergy();
}

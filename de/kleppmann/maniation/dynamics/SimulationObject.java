package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.Vector;

public interface SimulationObject {
    void setSimulationTime(double time);
    Vector getState(boolean rateOfChange);
    void setState(Vector state);
}

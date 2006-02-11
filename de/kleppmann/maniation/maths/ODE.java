package de.kleppmann.maniation.maths;

public interface ODE {
    Vector getInitial();
    Vector derivative(double time, Vector state) throws ODEBacktrackException;
    Vector impulse(double time, Vector state);
    void timeStepCompleted(double time, Vector state);
}

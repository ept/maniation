package de.kleppmann.maniation.maths;

public interface ODE {
    Vector getInitial();
    Vector derivative(double time, Vector state, boolean allowBacktrack)
        throws ODEBacktrackException;
    void timeStepCompleted(double time, Vector state);
}

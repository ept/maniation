package de.kleppmann.maniation.maths;

public interface ODE {
    Vector getInitial();
    Vector derivative(double time, Vector state);
    void timeStepCompleted(double time, Vector state);
}

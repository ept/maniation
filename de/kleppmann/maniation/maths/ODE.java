package de.kleppmann.maniation.maths;

public interface ODE {
    Vector getInitial();
    Vector derivative(double time, Vector state, boolean allowBacktrack) throws ODEBacktrackException;
    Vector timeStep(double time, Vector state, boolean allowBacktrack) throws ODEBacktrackException;
}

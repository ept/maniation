package de.kleppmann.maniation.maths;

public interface ODE {
    Vector derivative(double time, Vector state);
}

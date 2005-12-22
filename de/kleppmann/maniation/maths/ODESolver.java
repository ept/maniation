package de.kleppmann.maniation.maths;

public interface ODESolver {
    void setAccuracy(double requiredAccuracy);
    Vector getStatus();
    void setStatus(Vector status);
    double getTime();
    void setTime(double time);
    int solveUpTo(double time); // returns number of steps taken
}

package de.kleppmann.maniation.maths;

public interface ODESolver {
    void setAccuracy(double requiredAccuracy);
    void solve(double startTime, double finishTime);
}

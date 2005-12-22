package de.kleppmann.maniation.maths;

public class RungeKutta implements ODESolver {
    
    private ODE ode;
    private double time = 0.0;
    private double h, hmin = 1e-10, eps = 1e-6;

    public RungeKutta(ODE ode, double timeStepGuess) {
        this.ode = ode;
        this.h = timeStepGuess;
    }
    
    public void setMinTimeStep(double minTimeStep) {
        hmin = minTimeStep;
    }
    
    public void setAccuracy(double requiredAccuracy) {
        eps = requiredAccuracy;
    }

    public Vector getStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setStatus(Vector status) {
        // TODO Auto-generated method stub

    }

    public double getTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setTime(double time) {
        // TODO Auto-generated method stub

    }

    public int solveUpTo(double time) {
        // TODO Auto-generated method stub
        return 0;
    }

}

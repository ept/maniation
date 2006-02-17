package de.kleppmann.maniation.maths;

public class RungeKutta implements ODESolver {
    
    private ODE ode;
    private double time, timeStepGuess;
    private double h, hmin = 1e-4, eps = 1e-6;
    private Vector status;

    public RungeKutta(ODE ode, double timeStepGuess) {
        this.ode = ode;
        this.h = timeStepGuess;
        this.timeStepGuess = timeStepGuess;
        this.status = ode.getInitial();
    }
    
    public void setMinTimeStep(double minTimeStep) {
        hmin = minTimeStep;
    }
    
    public void setAccuracy(double requiredAccuracy) {
        eps = requiredAccuracy;
    }
    
    private void calcStep() throws ODEBacktrackException {
        Vector k1 = ode.derivative(time,         status                  ).mult(h);
        Vector k2 = ode.derivative(time + 0.5*h, status.add(k1.mult(0.5))).mult(h);
        Vector k3 = ode.derivative(time + 0.5*h, status.add(k2.mult(0.5))).mult(h);
        Vector k4 = ode.derivative(time +     h, status.add(k3)          ).mult(h);
        status = status.add(k1.mult(1/6.0).add(
                            k2.mult(1/3.0).add(
                            k3.mult(1/3.0).add(
                            k4.mult(1/6.0)))));
    }

    public void solve(double startTime, double finishTime) {
        time = startTime;
        while (time + hmin < finishTime) {
            if (time + h > finishTime) h = finishTime - time;
            try {
                System.out.print("Time " + time + " plus " + h + ": ");
                calcStep();
                time += h;
                ode.timeStepCompleted(time, status);
                System.out.println("completed.");
                h *= 2.0;
                if (h > timeStepGuess) h = timeStepGuess;
            } catch (ODEBacktrackException backtrack) {
                if (h < 2*hmin) {
                    System.out.println("Collision");
                    status = ode.impulse(time, status);
                } else {
                    System.out.println("Penetration");
                    if (h < hmin) throw new IllegalStateException();
                    if (backtrack.hasTimeEstimate()) {
                        double newh = backtrack.getTimeEstimate() - time;
                        if ((newh >= hmin) && (newh < 0.85*h)) h = newh; else h /= 2.0;
                    } else h /= 2.0;
                    if (h < hmin) h = hmin;
                }
            }
        }
    }
}

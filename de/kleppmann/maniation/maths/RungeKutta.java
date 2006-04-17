package de.kleppmann.maniation.maths;

import java.text.DecimalFormat;

public class RungeKutta implements ODESolver {
    
    private ODE ode;
    private double time;
    private double h, hnew, hmin = 0.00001, hmax = 0.1, eps = 1e-6, errorOffset = 0.01, safety = 0.9;
    private double shrinkPower = -0.25, growPower = -0.2;
    private Vector status, error;
    private boolean colliding = false;
    
    // Cash-Karp parameters for embedded Runge-Kutta
    private static final double a2 = 0.2, a3 = 0.3, a4 = 0.6, a5 = 1.0, a6 = 0.875,
        b21 = 0.2, b31 = 3.0/40.0, b32 = 9.0/40.0, b41 = 0.3, b42 = -0.9, b43 = 1.2,
        b51 = -11.0/54.0, b52 = 2.5, b53 = -70.0/27.0, b54 = 35.0/27.0,
        b61 = 1631.0/55296.0, b62 = 175.0/512.0, b63 = 575.0/13824.0,
        b64 = 44275.0/110592.0, b65 = 253.0/4096.0,
        c1 = 37.0/378.0, c3 = 250.0/621.0, c4 = 125.0/594.0, c6 = 512.0/1771.0;
    private static final double dc1 = c1 - 2825.0/27648.0, dc3 = c3 - 18575.0/48384.0,
        dc4 = c4 - 13525.0/55296.0, dc5 = -277.0/14336.0, dc6 = c6 - 0.25;
        

    public RungeKutta(ODE ode, double timeStepGuess) {
        this.ode = ode;
        this.h = timeStepGuess;
        this.status = ode.getInitial();
    }
    
    public void setMinTimeStep(double minTimeStep) {
        hmin = minTimeStep;
        if (h < hmin) h = hmin;
    }
    
    public void setMaxTimeStep(double maxTimeStep) {
        hmax = maxTimeStep;
        if (h > hmax) h = hmax;
    }
    
    public void setAccuracy(double requiredAccuracy) {
        eps = requiredAccuracy;
    }
    
    private Vector cashKarp(boolean allowBacktrack) throws ODEBacktrackException {
        Vector k1 = ode.derivative(time, status, allowBacktrack);
        Vector d2 = status.add(k1.mult(b21*h));
        Vector k2 = ode.derivative(time + a2*h, d2, allowBacktrack);
        Vector d3 = status.add(k1.mult(b31*h).add(k2.mult(b32*h)));
        Vector k3 = ode.derivative(time + a3*h, d3, allowBacktrack);
        Vector d4 = status.add(k1.mult(b41*h).add(k2.mult(b42*h)).add(k3.mult(b43*h)));
        Vector k4 = ode.derivative(time + a4*h, d4, allowBacktrack);
        Vector d5 = status.add(k1.mult(b51*h).add(k2.mult(b52*h)).add(k3.mult(b53*h)).add(k4.mult(b54*h)));
        Vector k5 = ode.derivative(time + a5*h, d5, allowBacktrack);
        Vector d6 = status.add(k1.mult(b61*h).add(k2.mult(b62*h)).add(k3.mult(b63*h)).add(k4.mult(b64*h)).
                add(k5.mult(b65*h)));
        Vector k6 = ode.derivative(time + a6*h, d6, allowBacktrack);
        error = k1.mult(dc1*h).add(k3.mult(dc3*h)).add(k4.mult(dc4*h)).add(k5.mult(dc5*h)).add(k6.mult(dc6*h));
        return status.add(k1.mult(c1*h).add(k3.mult(c3*h)).add(k4.mult(c4*h)).add(k6.mult(c6*h)));
    }
    
    private Vector calcStep(boolean allowBacktrack) throws ODEBacktrackException {
        double errmax;
        Vector newstatus;
        DecimalFormat format = new DecimalFormat("0.000000");
        while (true) {
            System.out.print("Time " + format.format(time) + " plus " + format.format(h) + ": ");
            newstatus = cashKarp(allowBacktrack);
            errmax = 0.0; int errIndex = -1;
            for (int i=0; i<newstatus.getDimension(); i++) {
                double scale = Math.abs(newstatus.getComponent(i)) + errorOffset;
                double e = Math.abs(error.getComponent(i)/scale);
                if (e > errmax) { errmax = e; errIndex = i; }
            }
            errmax /= eps;
            if (errmax <= 1.0) break;
            if (h < 1.3*hmin) {
                System.out.print("error too large (component " + errIndex + "), but continuing anyway: ");
                hnew = hmin;
                return newstatus;
            }
            System.out.println("error too large in component " + errIndex + ".");
            hnew = safety*h*Math.pow(errmax, shrinkPower);
            if (hnew < 0.1*h) hnew = 0.1*h;
            if (hnew < hmin) hnew = hmin;
            h = hnew;
        }
        if (!colliding) {
            hnew = safety*h*Math.pow(errmax, growPower);
            if (hnew > 2.0*h) hnew = 2.0*h;
            if (hnew > hmax) hnew = hmax;
        } else hnew = h;
        return newstatus;
    }

    public void solve(double startTime, double finishTime) {
        time = startTime;
        while (time + hmin < finishTime) {
            if (time + h > finishTime) h = finishTime - time;
            try {
                boolean allowBacktrack = false;//(h >= 1.1*hmin);
                status = ode.timeStep(time + h, calcStep(allowBacktrack), allowBacktrack);
                if (allowBacktrack) System.out.println("completed.");
                else System.out.println("completed (penetration ignored).");
                time += h; h = hnew; colliding = false;
            } catch (ODEBacktrackException backtrack) {
                if (h < 1.1*hmin) {
                    System.out.println("illegally signalled penetration when it should have been ignored.");
                } else {
                    System.out.println("penetration.");
                    colliding = true;
                    if (backtrack.hasTimeEstimate()) {
                        hnew = backtrack.getTimeEstimate() - time;
                        if (hnew < 0.85*h) h = hnew; else h /= 2.0;
                    } else h /= 2.0;
                    if (h < hmin) h = hmin;
                }
            }
        }
    }
}

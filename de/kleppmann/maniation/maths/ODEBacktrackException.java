package de.kleppmann.maniation.maths;

public class ODEBacktrackException extends Exception {
    private static final long serialVersionUID = 0L;

    private boolean hasEstimate;
    private double raiseTime, estimate;
    
    /*public ODEBacktrackException(double raiseTime) {
        this.raiseTime = raiseTime;
        hasEstimate = false;
    }*/
    
    public ODEBacktrackException(double raiseTime, double timeEstimate) {
        this.raiseTime = raiseTime;
        hasEstimate = true;
        estimate = timeEstimate;
    }
    
    public double getRaiseTime() {
        return raiseTime;
    }
    
    public boolean hasTimeEstimate() {
        return hasEstimate;
    }
    
    public double getTimeEstimate() {
        return estimate;
    }
}

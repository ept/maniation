package de.kleppmann.maniation.maths;

public class ODEBacktrackException extends Exception {
    private static final long serialVersionUID = 0L;
    
    private boolean hasEstimate;
    private double estimate;
    
    public ODEBacktrackException() {
        hasEstimate = false;
    }
    
    public ODEBacktrackException(double timeEstimate) {
        hasEstimate = true;
        estimate = timeEstimate;
    }
    
    public boolean hasTimeEstimate() {
        return hasEstimate;
    }
    
    public double getTimeEstimate() {
        return estimate;
    }
}

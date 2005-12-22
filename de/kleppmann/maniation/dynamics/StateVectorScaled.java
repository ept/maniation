package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.Vector;

class StateVectorScaled extends StateVector {
    
    private double factor;
    
    public StateVectorScaled(StateVector origin, double factor) {
        applyProperties(origin);
        this.factor = factor;
    }

    public double getComponent(int index) {
        return factor*super.getComponent(index);
    }

    public Vector mult(double scalar) {
        return new StateVectorScaled(this, factor*scalar);
    }
}

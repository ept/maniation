package de.kleppmann.maniation.dynamics;

import java.text.DecimalFormat;

import de.kleppmann.maniation.maths.Vector;

public class StateVector implements Vector {

    private Body[] bodies;
    private boolean rateOfChange;
    private int[] stateOffsets = {0};
    
    public StateVector(Body[] bodies, boolean rateOfChange) {
        this.bodies = bodies;
        this.rateOfChange = rateOfChange;
        updateObjects();
    }
    
    private void updateObjects() {
        int i=0, j=0;
        stateOffsets = new int[bodies.length + 1];
        for (Body body : bodies) {
            stateOffsets[i] = j;
            i++; j += body.getState(false).getDimension();
        }
        stateOffsets[i] = j;
    }
    
    Vector getSlice(int index) {
        return bodies[index].getState(rateOfChange);
    }
    
    public int getDimension() {
        return stateOffsets[stateOffsets.length - 1];
    }

    public double getComponent(int index) {
        // Naive linear search. Replace this by binary chop.
        int i = stateOffsets.length - 1;
        while (stateOffsets[i] > index) i--;
        return getSlice(i).getComponent(index - stateOffsets[i]);
    }

    public Vector mult(double scalar) {
        return new StateVectorModified(Operation.SCALE, this, this, scalar);
    }

    public double mult(Vector v) {
        throw new UnsupportedOperationException();
    }

    public Vector multComponents(Vector v) {
        throw new UnsupportedOperationException();
    }

    public Vector add(Vector v) {
        if (!(v instanceof StateVector)) throw new IllegalArgumentException();
        return new StateVectorModified(Operation.ADD, this, (StateVector) v, 0);
    }

    public Vector subtract(Vector v) {
        if (!(v instanceof StateVector)) throw new IllegalArgumentException();
        return new StateVectorModified(Operation.SUBTRACT, this, (StateVector) v, 0);
    }

    public void toDoubleArray(double[] array, int offset) {
        for (int i=0; i<getDimension(); i++) array[i+offset] = getComponent(i);
    }
    
    public void apply() {
        for (int i=0; i<bodies.length; i++) bodies[i].setState(getSlice(i));
    }
    
    public String toString() {
        DecimalFormat format = new DecimalFormat("######0.000000000000000");
        String result = "";
        for (int i=0; i<getDimension(); i++) {
            result += " " + format.format(getComponent(i));
        }
        return result;
    }
    
    
    private enum Operation { ADD, SUBTRACT, SCALE };
    
    private class StateVectorModified extends StateVector {
        private Vector[] slices;
        
        public StateVectorModified(Operation op, StateVector op1, StateVector op2, double factor) {
            super(op1.bodies, op1.rateOfChange && op2.rateOfChange);
            this.slices = new Vector[op1.bodies.length];
            for (int i=0; i<slices.length; i++) {
                if (op == Operation.ADD) slices[i] = op1.getSlice(i).add(op2.getSlice(i)); else
                if (op == Operation.SUBTRACT) slices[i] = op1.getSlice(i).subtract(op2.getSlice(i)); else
                if (op == Operation.SCALE) slices[i] = op1.getSlice(i).mult(factor);
            }
        }

        // Don't override getComponent as well!
        Vector getSlice(int index) {
            return slices[index];
        }
    }
}

package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.SlicedVector;
import de.kleppmann.maniation.maths.Vector;

public class StateVector extends SlicedVector<GeneralizedBody.State> {

    private final GeneralizedBody[] bodies;
    private final boolean rateOfChange;
    
    public StateVector(GeneralizedBody[] bodies) {
        super(initialState(bodies));
        this.bodies = bodies;
        this.rateOfChange = false;
    }

    private StateVector(GeneralizedBody[] bodies, SlicedVector<GeneralizedBody.State> values,
            boolean rateOfChange) {
        super(newState(values));
        this.bodies = bodies;
        this.rateOfChange = rateOfChange;
    }
    
    private StateVector(GeneralizedBody[] bodies, GeneralizedBody.State[] states,
            boolean rateOfChange) {
        super(states);
        this.bodies = bodies;
        this.rateOfChange = rateOfChange;
    }
    
    private static GeneralizedBody.State[] initialState(GeneralizedBody[] bodies) {
        GeneralizedBody.State[] states = new GeneralizedBody.State[bodies.length];
        for (int i=0; i<bodies.length; i++) states[i] = bodies[i].getInitialState();
        return states;
    }
    
    private static GeneralizedBody.State[] newState(SlicedVector<GeneralizedBody.State> values) {
        GeneralizedBody.State[] states = new GeneralizedBody.State[values.getSlices()];
        for (int i=0; i<values.getSlices(); i++) states[i] = values.getSlice(i);
        return states;
    }

    public StateVector getDerivative() {
        if (rateOfChange) throw new UnsupportedOperationException();
        GeneralizedBody.State[] states = new GeneralizedBody.State[bodies.length];
        for (int i=0; i<getSlices(); i++) states[i] = getSlice(i).getDerivative();
        return new StateVector(bodies, states, true);
    }

    public StateVector mult(double scalar) {
        return new StateVector(bodies, super.mult(scalar), rateOfChange);
    }

    public StateVector add(Vector v) {
        if (!(v instanceof StateVector)) throw new IllegalArgumentException();
        StateVector other = (StateVector) v;
        if (this.bodies != other.bodies) throw new IllegalArgumentException();
        return new StateVector(bodies, super.add(v), this.rateOfChange && other.rateOfChange);
    }

    public StateVector subtract(Vector v) {
        if (!(v instanceof StateVector)) throw new IllegalArgumentException();
        StateVector other = (StateVector) v;
        if (this.bodies != other.bodies) throw new IllegalArgumentException();
        return new StateVector(bodies, super.subtract(v), this.rateOfChange && other.rateOfChange);
    }
}

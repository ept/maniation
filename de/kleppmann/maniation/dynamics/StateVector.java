package de.kleppmann.maniation.dynamics;

import java.util.Map;

import de.kleppmann.maniation.dynamics.GeneralizedBody.State;
import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.SlicedVector;
import de.kleppmann.maniation.maths.SparseMatrix;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.maths.VectorImpl;

public class StateVector extends SlicedVector<GeneralizedBody.State> implements GeneralizedBody.State {

    private final GeneralizedBody owner;
    private final GeneralizedBody[] bodies;
    private final boolean rateOfChange;
    private Map<GeneralizedBody, GeneralizedBody.State> stateMap;
    private Map<GeneralizedBody, Integer> bodyIndices, bodyOffsets;
    private Map<GeneralizedBody, StateVector> nestedBodies;
    private SlicedVector<Vector> veloc, accel;
    private SparseMatrix massInertia;
    private double energy;
    
    public StateVector(GeneralizedBody owner, GeneralizedBody[] bodies) {
        super(initialState(bodies));
        this.owner = owner;
        this.bodies = bodies;
        this.rateOfChange = false;
        update();
    }

    private StateVector(GeneralizedBody owner, GeneralizedBody[] bodies,
            SlicedVector<GeneralizedBody.State> values, boolean rateOfChange) {
        super(newState(values));
        this.owner = owner;
        this.bodies = bodies;
        this.rateOfChange = rateOfChange;
        update();
    }
    
    private StateVector(GeneralizedBody owner, GeneralizedBody[] bodies,
            GeneralizedBody.State[] states, boolean rateOfChange) {
        super(states);
        this.owner = owner;
        this.bodies = bodies;
        this.rateOfChange = rateOfChange;
        update();
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
    
    private void update() {
        if (rateOfChange) {
            bodyIndices = bodyOffsets = null; stateMap = null;
            return;
        }
        bodyIndices = new java.util.HashMap<GeneralizedBody, Integer>();
        bodyOffsets = new java.util.HashMap<GeneralizedBody, Integer>();
        nestedBodies = new java.util.HashMap<GeneralizedBody, StateVector>();
        stateMap = new java.util.HashMap<GeneralizedBody, GeneralizedBody.State>();
        Vector[] velarray = new Vector[bodies.length];
        Vector[] accarray = new Vector[bodies.length];
        SparseMatrix.Slice[] mass = new SparseMatrix.Slice[bodies.length];
        energy = 0.0;
        int offset = 0;
        // For all bodies
        for (int i=0; i<bodies.length; i++) {
            GeneralizedBody.State state = getSlice(i);
            bodyIndices.put(bodies[i], i);
            // stateMap and offsetMap: include sub-bodies
            bodyOffsets.put(bodies[i], offset);
            stateMap.put(bodies[i], state);
            if (state instanceof StateVector) {
                StateVector sv = (StateVector) state;
                stateMap.putAll(sv.getStateMap());
                for (Map.Entry<GeneralizedBody, Integer> bodyOffset : sv.getOffsetMap().entrySet()) {
                    bodyOffsets.put(bodyOffset.getKey(), bodyOffset.getValue() + offset);
                    nestedBodies.put(bodyOffset.getKey(), sv);
                }
            }
            // Velocity & acceleration vectors, mass/inertia tensor, total energy
            velarray[i] = state.getVelocities();
            accarray[i] = state.getAccelerations();
            mass[i] = new SparseMatrix.SliceImpl(state.getMassInertia(), offset, offset);
            energy += state.getEnergy();
            // New offset
            offset += velarray[i].getDimension();
        }
        veloc = new SlicedVector<Vector>(velarray);
        accel = new SlicedVector<Vector>(accarray);
        massInertia = new SparseMatrix(offset, offset, mass);
    }
    
    private StateVector replaceState(GeneralizedBody body, GeneralizedBody.State newState) {
        GeneralizedBody.State[] states = new GeneralizedBody.State[bodies.length];
        for (int i=0; i<bodies.length; i++) states[i] = getSlice(i);
        Integer index = bodyIndices.get(body);
        if (index != null) states[index] = newState;
        else {
            StateVector nested = nestedBodies.get(body);
            if (nested != null) index = bodyIndices.get(nested.getOwner());
            if (index != null) states[index] = nested.replaceState(body, newState);
        }
        return new StateVector(owner, bodies, states, rateOfChange);
    }

    public Map<GeneralizedBody, GeneralizedBody.State> getStateMap() {
        if (rateOfChange) throw new UnsupportedOperationException();
        return stateMap;
    }
    
    public Map<GeneralizedBody, Integer> getOffsetMap() {
        if (rateOfChange) throw new UnsupportedOperationException();
        return bodyOffsets;
    }
    
    public StateVector getDerivative() {
        if (rateOfChange) throw new UnsupportedOperationException();
        GeneralizedBody.State[] states = new GeneralizedBody.State[bodies.length];
        for (int i=0; i<getSlices(); i++) states[i] = getSlice(i).getDerivative();
        return new StateVector(owner, bodies, states, true);
    }
    
    public StateVector handleInteraction(Interaction interaction) {
        if (rateOfChange) throw new UnsupportedOperationException();
        StateVector state = this;
        for (SimulationObject obj : interaction.getObjects()) {
            if (obj instanceof GeneralizedBody) {
                GeneralizedBody body = (GeneralizedBody) obj;
                SimulationObject.State newState = body.handleInteraction(state.stateMap.get(body), interaction);
                if (newState instanceof GeneralizedBody.State)
                    state = state.replaceState(body, (GeneralizedBody.State) newState);
            }
        }
        return state;
    }
    
    @Override
    public StateVector mult(double scalar) {
        return new StateVector(owner, bodies, super.mult(scalar), rateOfChange);
    }

    @Override
    public StateVector add(Vector v) {
        if (!(v instanceof StateVector)) throw new IllegalArgumentException();
        StateVector other = (StateVector) v;
        if (this.bodies != other.bodies) throw new IllegalArgumentException();
        return new StateVector(owner, bodies, super.add(v), this.rateOfChange && other.rateOfChange);
    }

    @Override
    public StateVector subtract(Vector v) {
        if (!(v instanceof StateVector)) throw new IllegalArgumentException();
        StateVector other = (StateVector) v;
        if (this.bodies != other.bodies) throw new IllegalArgumentException();
        return new StateVector(owner, bodies, super.subtract(v), this.rateOfChange && other.rateOfChange);
    }
    
    private StateVector applyImpulseOrForce(Vector value, boolean impulse) {
        GeneralizedBody.State[] states = new GeneralizedBody.State[bodies.length];
        double[] array = new double[value.getDimension()];
        value.toDoubleArray(array, 0);
        int offset = 0;
        for (int i=0; i<bodies.length; i++) {
            GeneralizedBody.State state = getSlice(i);
            int size = state.getVelocities().getDimension();
            double[] vec = new double[size];
            for (int j=0; j<size; j++) vec[j] = array[offset+j];
            if (impulse) states[i] = state.applyImpulse(new VectorImpl(vec));
            else states[i] = state.applyForce(new VectorImpl(vec));
            offset += size;
        }
        return new StateVector(owner, bodies, states, rateOfChange);
    }

    public StateVector applyForce(Vector forceTorque) {
        return applyImpulseOrForce(forceTorque, false);
    }

    public StateVector applyImpulse(Vector impulse) {
        return applyImpulseOrForce(impulse, true);
    }

    public State applyPosition(Map<Body, Vector3D> newPositions) {
        GeneralizedBody.State[] states = new GeneralizedBody.State[bodies.length];
        for (int i=0; i<bodies.length; i++)
            states[i] = getSlice(i).applyPosition(newPositions);
        return new StateVector(owner, bodies, states, rateOfChange);
    }

    public Vector getVelocities() {
        if (rateOfChange) throw new UnsupportedOperationException();
        return veloc;
    }

    public Vector getAccelerations() {
        if (rateOfChange) throw new UnsupportedOperationException();
        return accel;
    }

    public double getEnergy() {
        if (rateOfChange) throw new UnsupportedOperationException();
        return energy;
    }

    public Matrix getMassInertia() {
        if (rateOfChange) throw new UnsupportedOperationException();
        return massInertia;
    }

    public GeneralizedBody getOwner() {
        return owner;
    }

    public StateVector load(Vector input) {
        if (input.getDimension() != this.getDimension()) throw new IllegalArgumentException();
        GeneralizedBody.State[] newStates = new GeneralizedBody.State[bodies.length];
        double[] arr = new double[getDimension()];
        input.toDoubleArray(arr, 0);
        int offs = 0;
        for (int i=0; i<bodies.length; i++) {
            GeneralizedBody.State state = getSlice(i);
            double[] subarr = new double[state.getDimension()];
            for (int j=0; j<subarr.length; j++) subarr[j] = arr[offs+j];
            newStates[i] = state.load(new VectorImpl(subarr));
            offs += state.getDimension();
        }
        return new StateVector(owner, bodies, newStates, false);
    }
}

package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.SparseMatrix;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.VectorImpl;

public class CompoundBody implements GeneralizedBody {
    
    private GeneralizedBody[] bodies;
    private int dimensions, stateSize;
    private AccelerationVector accelerationVector;
    private VelocityVector velocityVector;
    private SparseMatrix massInertia;
    private StateVector state, stateDot;
    
    public CompoundBody(GeneralizedBody[] bodies) {
        this.bodies = bodies;
        dimensions = 0; stateSize = 0;
        for (GeneralizedBody b : bodies) {
            dimensions += b.getVelocities().getDimension();
            stateSize += b.getState(false).getDimension();
        }
        accelerationVector = new AccelerationVector();
        velocityVector = new VelocityVector();
        SparseMatrix.Slice[] slices = new SparseMatrix.Slice[bodies.length];
        int i = 0, offset = 0;
        for (GeneralizedBody b : bodies) {
            slices[i] = new SparseMatrix.SliceImpl(b.getMassInertia(), offset, offset);
            i++; offset += b.getVelocities().getDimension();
        }
        massInertia = new SparseMatrix(dimensions, dimensions, slices);
        state = new StateVector(bodies, false);
        stateDot = new StateVector(bodies, true);
    }

    public Vector getVelocities() {
        return velocityVector;
    }

    public Vector getAccelerations() {
        return accelerationVector;
    }

    public Matrix getMassInertia() {
        return massInertia;
    }

    public void applyForce(Vector forceTorque) {
        int offset = 0;
        for (GeneralizedBody b : bodies) {
            int size = b.getVelocities().getDimension();
            double[] vec = new double[size];
            for (int i=0; i<size; i++) vec[i] = forceTorque.getComponent(offset+i);
            b.applyForce(new VectorImpl(vec));
            offset += size;
        }
    }

    public void applyImpulse(Vector impulse) {
        int offset = 0;
        for (GeneralizedBody b : bodies) {
            int size = b.getVelocities().getDimension();
            double[] vec = new double[size];
            for (int i=0; i<size; i++) vec[i] = impulse.getComponent(offset+i);
            b.applyImpulse(new VectorImpl(vec));
            offset += size;
        }
    }

    public double getEnergy() {
        double sum = 0;
        for (GeneralizedBody b : bodies) sum += b.getEnergy();
        return sum;
    }

    public void setSimulationTime(double time) {
        for (GeneralizedBody b : bodies) b.setSimulationTime(time);
    }

    public Vector getState(boolean rateOfChange) {
        if (rateOfChange) return stateDot; else return state;
    }

    public void setState(Vector state) {
        if (state instanceof StateVector) {
            ((StateVector) state).apply();
        } else throw new IllegalArgumentException();
    }

    public void interaction(SimulationObject partner, InteractionList result, boolean allowReverse) {
        for (GeneralizedBody b : bodies) b.interaction(partner, result, true);
    }

    public void handleInteraction(Interaction action) {
        if (action instanceof InteractionForce)
            applyForce(((InteractionForce) action).getForceTorque(this));
    }
    
    
    private class AccelerationVector extends VectorImpl {
        public AccelerationVector() {
            super(null);
        }
        
        public int getDimension() {
            return dimensions;
        }

        public double getComponent(int index) {
            int offset = 0;
            for (GeneralizedBody b : bodies) {
                if (offset + b.getAccelerations().getDimension() > index) {
                    return b.getAccelerations().getComponent(index - offset);
                }
                offset += b.getAccelerations().getDimension();
            }
            throw new IllegalArgumentException();
        }
    }


    private class VelocityVector extends VectorImpl {
        public VelocityVector() {
            super(null);
        }
        
        public int getDimension() {
            return dimensions;
        }

        public double getComponent(int index) {
            int offset = 0;
            for (GeneralizedBody b : bodies) {
                if (offset + b.getVelocities().getDimension() > index) {
                    return b.getVelocities().getComponent(index - offset);
                }
                offset += b.getVelocities().getDimension();
            }
            throw new IllegalArgumentException();
        }
    }
}

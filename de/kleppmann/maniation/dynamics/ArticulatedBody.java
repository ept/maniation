package de.kleppmann.maniation.dynamics;

import java.util.List;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.SparseMatrix;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.VectorImpl;

public class ArticulatedBody implements Body {
    
    private List<Body> bodies = new java.util.ArrayList<Body>();
    private int dimensions, stateSize;
    private AccelerationVector accelerationVector;
    private VelocityVector velocityVector;
    private SparseMatrix massInertia;
    private StateVector state, stateDot;
    
    private void update() {
        dimensions = 0; stateSize = 0;
        for (Body b : bodies) {
            dimensions += b.getVelocities().getDimension();
            stateSize += b.getState(false).getDimension();
        }
        accelerationVector = new AccelerationVector();
        velocityVector = new VelocityVector();
        SparseMatrix.Slice[] slices = new SparseMatrix.Slice[bodies.size()];
        int i = 0, offset = 0;
        for (Body b : bodies) {
            slices[i] = new SparseMatrix.SliceImpl(b.getMassInertia(), offset, offset);
            i++; offset += b.getVelocities().getDimension();
        }
        massInertia = new SparseMatrix(dimensions, dimensions, slices);
        Body[] array = new Body[bodies.size()];
        array = bodies.toArray(array);
        state = new StateVector(array, false);
        stateDot = new StateVector(array, true);
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
        // TODO Auto-generated method stub
    }

    public void applyImpulse(Vector impulse) {
        // TODO Auto-generated method stub
    }

    public double getEnergy() {
        double sum = 0;
        for (Body b : bodies) sum += b.getEnergy();
        return sum;
    }

    public void setSimulationTime(double time) {
        for (Body b : bodies) b.setSimulationTime(time);
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
        // TODO Auto-generated method stub

    }

    public void handleInteraction(Interaction action) {
        // TODO Auto-generated method stub

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
            for (Body b : bodies) {
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
            for (Body b : bodies) {
                if (offset + b.getVelocities().getDimension() > index) {
                    return b.getVelocities().getComponent(index - offset);
                }
                offset += b.getVelocities().getDimension();
            }
            throw new IllegalArgumentException();
        }
    }
}

package de.kleppmann.maniation.dynamics;

import java.text.DecimalFormat;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.Matrix33;
import de.kleppmann.maniation.maths.MatrixImpl;
import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.maths.VectorImpl;

public abstract class RigidBody implements Body {

    // Primary quantities
    private Vector3D pos, mom, angmom;
    private Quaternion orient; // orient transforms local to world
    private boolean upToDate = false;
    // Derived quantities
    private Vector3D vel, angvel, accel, angaccel;
    private Quaternion orientDot;
    // Variable by simulation
    private Vector3D forces, torques;
    // Unchanging variables
    private AccelerationVector accelerationVector = new AccelerationVector();
    private VelocityVector velocityVector = new VelocityVector();
    private MassInertia massInertia = new MassInertia();
    private MassInertia invMassInertia = new InverseMassInertia();
    private State state = new State(false), stateDot = new State(true);

    public RigidBody() {
        pos = new Vector3D();
        mom = new Vector3D();
        angmom = new Vector3D();
        orient = new Quaternion();
        forces = new Vector3D();
        torques = new Vector3D();
    }
    
    protected abstract double getMass();
    protected abstract Matrix33 getInertia();
    protected abstract Matrix33 getInvInertia();
    
    private void deriveQuantities() {
        if (upToDate) return;
        vel = mom.mult(1.0/getMass());
        angvel = getInvInertia().mult(angmom);
        orientDot = (new Quaternion(angvel.mult(0.5))).mult(orient);
        accel = forces.mult(1.0/getMass());
        angaccel = getInvInertia().mult(torques).subtract(angvel.cross(angmom));
        upToDate = true;
    }

    public double getEnergy() {
        deriveQuantities();
        double potential = -getMass()*World.GRAVITY.mult(getCoMPosition());
        double kinetic = 0.5*getMass()*getCoMVelocity().mult(getCoMVelocity());
        double rotational = 0.5*getAngularVelocity().mult(getAngularMomentum());
        return potential + kinetic + rotational;
    }
    
    public Vector3D getLocation() {
        return pos;
    }

    public Vector3D getCoMPosition() {
        return pos;
    }
    
    protected void setCoMPosition(Vector3D pos) {
        this.pos = pos;
        upToDate = false;
    }
    
    public Vector3D getCoMVelocity() {
        deriveQuantities();
        return vel;
    }
    
    public Vector3D getLinearMomentum() {
        return mom;
    }
    
    protected void setLinearMomentum(Vector3D mom) {
        this.mom = mom;
        upToDate = false;
    }
    
    public Vector3D getForces() {
        return forces;
    }
    
    public Quaternion getOrientation() {
        return orient;
    }
    
    protected void setOrientation(Quaternion orient) {
        this.orient = orient;
        upToDate = false;
    }
    
    public Quaternion getRateOfRotation() {
        deriveQuantities();
        return orientDot;
    }
    
    public Vector3D getAngularVelocity() {
        deriveQuantities();
        return angvel;
    }
    
    public Vector3D getAngularMomentum() {
        return angmom;
    }
    
    protected void setAngularMomentum(Vector3D angmom) {
        this.angmom = angmom;
        upToDate = false;
    }
    
    public Vector3D getTorques() {
        return torques;
    }
    
    public Matrix getMassInertia() {
        return massInertia;
    }

    public Vector getState(boolean rateOfChange) {
        if (rateOfChange) return stateDot; else return state;
    }

    public void setSimulationTime(double time) {
        this.forces = new Vector3D();
        this.torques = new Vector3D();
        upToDate = false;
    }

    public void setState(Vector state) {
        if (state instanceof State) {
            ((State) state).applyState();
        } else throw new IllegalArgumentException();
    }

    public void applyForce(Vector forceTorque) {
        forces = forces.add(new Vector3D(forceTorque.getComponent(0), forceTorque.getComponent(1),
                forceTorque.getComponent(2)));
        torques = torques.add(new Vector3D(forceTorque.getComponent(3), forceTorque.getComponent(4),
                forceTorque.getComponent(5)));
        upToDate = false;
    }

    public void applyImpulse(Vector impulse) {
        setLinearMomentum(getLinearMomentum().add(new Vector3D(impulse.getComponent(0),
                impulse.getComponent(1), impulse.getComponent(2))));
        setAngularMomentum(getAngularMomentum().add(new Vector3D(impulse.getComponent(3),
                impulse.getComponent(4), impulse.getComponent(5))));
    }

    public Vector getAccelerations() {
        return accelerationVector;
    }

    public Vector getVelocities() {
        return velocityVector;
    }

    public void handleInteraction(Interaction action) {
        if (action instanceof InteractionForce)
            applyForce(((InteractionForce) action).getForceTorque(this));
    }

    public void interaction(SimulationObject partner, InteractionList result, boolean allowReverse) {
        if (allowReverse) partner.interaction(this, result, false);
    }
    
    
    private class AccelerationVector extends VectorImpl {
        public AccelerationVector() {
            super(null);
        }
        
        public int getDimension() {
            return 6;
        }

        public double getComponent(int index) {
            deriveQuantities();
            if (index < 3) return accel.getComponent(index);
            return angaccel.getComponent(index - 3);
        }
    }


    private class VelocityVector extends VectorImpl {
        public VelocityVector() {
            super(null);
        }
        
        public int getDimension() {
            return 6;
        }

        public double getComponent(int index) {
            if (index < 3) return getCoMVelocity().getComponent(index);
            return getAngularVelocity().getComponent(index - 3);
        }
    }
    
    
    private class MassInertia extends MatrixImpl {
        private MassInertia() {
            super(null);
        }
        public int getRows() {
            return 6;
        }
        public int getColumns() {
            return 6;
        }

        public double getComponent(int row, int column) {
            if ((row < 3) || (column < 3)) {
                if (row == column) return getMass();
                return 0.0;
            }
            return getInertia().getComponent(row - 3, column - 3);
        }

        public Matrix inverse() {
            return invMassInertia;
        }        
    }
    
    
    private class InverseMassInertia extends MassInertia {
        public double getComponent(int row, int column) {
            if ((row < 3) || (column < 3)) {
                if (row == column) return 1.0/getMass();
                return 0.0;
            }
            return getInvInertia().getComponent(row - 3, column - 3);
        }

        public Matrix inverse() {
            return massInertia;
        }
    }
    
    
    private class State extends VectorImpl {
        private boolean rateOfChange;

        State(boolean rateOfChange) {
            super(null);
            this.rateOfChange = rateOfChange;
        }

        public int getDimension() {
            return 13;
        }

        public double getComponent(int index) {
            if (!rateOfChange) {
                if (index < 3)  return getCoMPosition().getComponent(index);
                if (index == 3) return getOrientation().getW();
                if (index == 4) return getOrientation().getX();
                if (index == 5) return getOrientation().getY();
                if (index == 6) return getOrientation().getZ();
                if (index < 10) return getLinearMomentum().getComponent(index - 7);
                return getAngularMomentum().getComponent(index - 10);
            } else {
                if (index < 3)  return getCoMVelocity().getComponent(index);
                if (index == 3) return getRateOfRotation().getW();
                if (index == 4) return getRateOfRotation().getX();
                if (index == 5) return getRateOfRotation().getY();
                if (index == 6) return getRateOfRotation().getZ();
                if (index < 10) return getForces().getComponent(index - 7);
                return getTorques().getComponent(index - 10);
            }
        }

        public Vector add(Vector v) {
            if (v instanceof State) {
                return new StateSum(this, (State) v, false);
            } else throw new IllegalArgumentException();
        }

        public Vector subtract(Vector v) {
            if (v instanceof State) {
                return new StateSum(this, (State) v, true);
            } else throw new IllegalArgumentException();
        }

        public Vector mult(double scalar) {
            return new StateScaled(this, scalar);
        }
        
        void applyState() {
            if (rateOfChange) throw new UnsupportedOperationException();
            setCoMPosition(new Vector3D(getComponent(0), getComponent(1), getComponent(2)));
            setOrientation(new Quaternion(getComponent(3), getComponent(4),
                    getComponent(5), getComponent(6)));
            setLinearMomentum(new Vector3D(getComponent(7), getComponent(8), getComponent(9)));
            setAngularMomentum(new Vector3D(getComponent(10), getComponent(11), getComponent(12)));
        }
        
        public String toString() {
            DecimalFormat format = new DecimalFormat("######0.000000000000000");
            String result = "";
            for (int i=0; i<getDimension(); i++) {
                result += " " + format.format(getComponent(i));
            }
            return result;
        }
    }
    
    
    private class StateScaled extends State {
        private State originalState;
        private double factor;
        
        StateScaled(State originalState, double factor) {
            super(true); // scaling only makes sense for a derivative
            this.originalState = originalState;
            this.factor = factor;
        }
        
        public double getComponent(int index) {
            return factor*originalState.getComponent(index);
        }

        public Vector mult(double scalar) {
            return new StateScaled(originalState, factor*scalar);
        }
    }
    
    
    private class StateSum extends State {
        double[] values;
        
        StateSum(State s1, State s2, boolean difference) {
            super(s1.rateOfChange && s2.rateOfChange);
            if (s1.rateOfChange && !s2.rateOfChange) {
                State tmp = s1; s1 = s2; s2 = tmp;
            }
            values = new double[13];
            for (int i=0; i<values.length; i++) {
                if (!difference) values[i] = s1.getComponent(i) + s2.getComponent(i);
                else values[i] = s1.getComponent(i) - s2.getComponent(i);
            }
            if (!s1.rateOfChange && s2.rateOfChange) {
                Quaternion q1 = new Quaternion(s1.getComponent(3), s1.getComponent(4),
                        s1.getComponent(5), s1.getComponent(6));
                Quaternion q2;
                if (!difference)
                    q2 = new Quaternion(s2.getComponent(3), s2.getComponent(4),
                            s2.getComponent(5), s2.getComponent(6)); else
                    q2 = new Quaternion(-s2.getComponent(3), -s2.getComponent(4),
                            -s2.getComponent(5), -s2.getComponent(6));
                Quaternion q = q1.quergs(q2);
                values[3] = q.getW();
                values[4] = q.getX();
                values[5] = q.getY();
                values[6] = q.getZ();
            }
        }

        public double getComponent(int index) {
            return values[index];
        }
    }
}

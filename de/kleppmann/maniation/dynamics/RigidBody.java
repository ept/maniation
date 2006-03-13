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

    protected abstract Vector3D getInitialPosition();
    protected abstract Quaternion getInitialOrientation();
    protected abstract Vector3D getInitialLinearMomentum();
    protected abstract Vector3D getInitialAngularMomentum();
    protected abstract double getMass();
    protected abstract Matrix33 getInertia(Body.State state);
    protected abstract Matrix33 getInvInertia(Body.State state);
    
    public Body.State getInitialState() {
        return new State(getInitialPosition(), getInitialOrientation(),
                getInitialLinearMomentum(), getInitialAngularMomentum(), false);
    }

    public void interaction(SimulationObject.State ownState, SimulationObject.State partnerState,
            InteractionList result, boolean allowReverse) {
        if (allowReverse) partnerState.getOwner().interaction(partnerState, ownState, result, false);
    }
    
    public State handleInteraction(SimulationObject.State previousState, Interaction action) {
        State me;
        try {
            me = (State) previousState;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
        if ((me.getOwner() != this) || me.rateOfChange) throw new IllegalArgumentException();
        if (action instanceof InteractionForce) {
            InteractionForce f = (InteractionForce) action;
            return me.applyForce(f.getForceTorque(me.getOwner()));
        }
        return me;
    }
    
    
    class State implements Body.State {
        // Primary quantities
        private final Vector3D pos, mom, angmom;
        private final Quaternion orient; // orient transforms local to world
        private final Vector3D forces, torques;
        private final boolean rateOfChange;
        // Derived quantities
        private final Vector3D vel, angvel, accel, angaccel;
        private final Quaternion orientDot;
        private final Vector vel2, accel2;
        private final MassInertia massInertia;
        
        State(Vector3D pos, Quaternion orient, Vector3D mom, Vector3D angmom, boolean rateOfChange) {
            if (!rateOfChange) {
                this.pos = pos; this.orient = orient; this.mom = mom; this.angmom = angmom;
                this.forces = new Vector3D(); this.torques = new Vector3D();
                this.rateOfChange = rateOfChange;
                vel = mom.mult(1.0/getMass());
                angvel = getInvInertia(this).mult(angmom);
                orientDot = (new Quaternion(angvel.mult(0.5))).mult(orient);
                accel = forces.mult(1.0/getMass());
                angaccel = getInvInertia(this).mult(torques).subtract(angvel.cross(angmom));
                double[] velV = new double[6];
                vel.toDoubleArray(velV, 0); angvel.toDoubleArray(velV, 3);
                vel2 = new VectorImpl(velV);
                double[] accelV = new double[6];
                accel.toDoubleArray(accelV, 0); angaccel.toDoubleArray(accelV, 3);
                accel2 = new VectorImpl(accelV);
                massInertia = new MassInertia(this);
            } else {
                this.pos = null; this.orient = null; this.mom = pos.mult(getMass());
                this.angmom = null; this.forces = mom; this.torques = angmom;
                this.rateOfChange = rateOfChange; this.vel = pos; this.angvel = null;
                this.accel = forces.mult(1.0/getMass()); this.angaccel = null;
                this.orientDot = orient; this.vel2 = null; this.accel2 = null;
                this.massInertia = null;
            }
        }
        
        State(State origin, Vector3D linear, Vector3D angular, boolean impulse) {
            this.pos = origin.pos; this.orient = origin.orient; rateOfChange = false;
            if (impulse) {
                this.mom = origin.mom.add(linear); this.angmom = origin.angmom.add(angular);
                this.forces = origin.forces; this.torques = origin.torques;
                vel = mom.mult(1.0/getMass());
                angvel = getInvInertia(this).mult(angmom);
                orientDot = (new Quaternion(angvel.mult(0.5))).mult(orient);
                this.accel = origin.accel; this.angaccel = origin.angaccel;
            } else {
                this.mom = origin.mom; this.angmom = origin.angmom;
                this.forces = origin.forces.add(linear); this.torques = origin.torques.add(angular);
                this.vel = origin.vel; this.angvel = origin.angvel; this.orientDot = origin.orientDot;
                accel = forces.mult(1.0/getMass());
                angaccel = getInvInertia(this).mult(torques).subtract(angvel.cross(angmom));
            }
            double[] velV = new double[6];
            vel.toDoubleArray(velV, 0); angvel.toDoubleArray(velV, 3);
            vel2 = new VectorImpl(velV);
            double[] accelV = new double[6];
            accel.toDoubleArray(accelV, 0); angaccel.toDoubleArray(accelV, 3);
            accel2 = new VectorImpl(accelV);
            massInertia = new MassInertia(this);
        }
        
        State(State origin, boolean rateOfChange) {
            this.pos = origin.pos; this.mom = origin.mom; this.angmom = origin.angmom;
            this.orient = origin.orient; this.forces = origin.forces; this.torques = origin.torques;
            this.rateOfChange = rateOfChange;
            this.vel = origin.vel; this.angvel = origin.angvel; this.accel = origin.accel;
            this.angaccel = origin.angaccel; this.orientDot = origin.orientDot;
            this.vel2 = origin.vel2; this.accel2 = origin.accel2;
            this.massInertia = origin.massInertia;
        }
        
        public Body getOwner() {
            return RigidBody.this;
        }
        
        public double getEnergy() {
            double potential = -getMass()*World.GRAVITY.mult(pos);
            double kinetic = 0.5*getMass()*vel.mult(vel);
            double rotational = 0.5*angvel.mult(angmom);
            return potential + kinetic + rotational;
        }

        public Vector3D getCoMPosition() {
            if (rateOfChange) throw new UnsupportedOperationException();
            return pos;
        }

        public Vector3D getCoMVelocity() {
            if (rateOfChange) throw new UnsupportedOperationException();
            return vel;
        }

        public Quaternion getOrientation() {
            if (rateOfChange) throw new UnsupportedOperationException();
            return orient;
        }

        public Vector3D getAngularVelocity() {
            if (rateOfChange) throw new UnsupportedOperationException();
            return angvel;
        }

        public State applyForce(Vector forceTorque) {
            if (rateOfChange) throw new UnsupportedOperationException();
            Vector3D force = new Vector3D(forceTorque.getComponent(0),
                    forceTorque.getComponent(1), forceTorque.getComponent(2));
            Vector3D torque = new Vector3D(forceTorque.getComponent(3),
                    forceTorque.getComponent(4), forceTorque.getComponent(5));
            return new State(this, force, torque, false);
        }

        public State applyImpulse(Vector impulse) {
            if (rateOfChange) throw new UnsupportedOperationException();
            Vector3D linear = new Vector3D(impulse.getComponent(0),
                    impulse.getComponent(1), impulse.getComponent(2));
            Vector3D angular = new Vector3D(impulse.getComponent(3),
                    impulse.getComponent(4), impulse.getComponent(5));
            return new State(this, linear, angular, true);
        }

        public State getDerivative() {
            return new State(this, true);
        }

        public Matrix getMassInertia() {
            if (rateOfChange) throw new UnsupportedOperationException();
            return massInertia;
        }

        public Vector getVelocities() {
            if (rateOfChange) throw new UnsupportedOperationException();
            return vel2;
        }

        public Vector getAccelerations() {
            if (rateOfChange) throw new UnsupportedOperationException();
            return accel2;
        }

        public State add(Vector v) {
            if (!(v instanceof State)) throw new IllegalArgumentException();
            // If we are performing an integration step, v2 should be the derivative.
            // Otherwise the order is arbitrary.
            State v1 = this;
            State v2 = (State) v;
            if (v1.rateOfChange && !v2.rateOfChange) {
                State tmp = v1; v1 = v2; v2 = tmp;
            }
            // Gather the quantities that we are adding
            Vector3D   p1 = v1.rateOfChange ? v1.vel       : v1.pos;
            Vector3D   p2 = v2.rateOfChange ? v2.vel       : v2.pos;
            Quaternion q1 = v1.rateOfChange ? v1.orientDot : v1.orient;
            Quaternion q2 = v2.rateOfChange ? v2.orientDot : v2.orient;
            Vector3D   r1 = v1.rateOfChange ? v1.forces    : v1.mom;
            Vector3D   r2 = v2.rateOfChange ? v2.forces    : v2.mom;
            Vector3D   s1 = v1.rateOfChange ? v1.torques   : v1.angmom;
            Vector3D   s2 = v2.rateOfChange ? v2.torques   : v2.angmom;
            // Deal with that quaternion
            Quaternion q;
            if (!v1.rateOfChange && v2.rateOfChange) q = q1.quergs(q2);
            else q = new Quaternion(q1.getW() + q2.getW(), q1.getX() + q2.getX(),
                    q1.getY() + q2.getY(), q1.getZ() + q2.getZ());
            // Create sum
            return new State(p1.add(p2), q, r1.add(r2), s1.add(s2),
                    v1.rateOfChange && v2.rateOfChange);
        }

        public double getComponent(int index) {
            if (!rateOfChange) {
                if (index < 3)  return pos.getComponent(index);
                if (index == 3) return orient.getW();
                if (index == 4) return orient.getX();
                if (index == 5) return orient.getY();
                if (index == 6) return orient.getZ();
                if (index < 10) return mom.getComponent(index - 7);
                return angmom.getComponent(index - 10);
            } else {
                if (index < 3)  return vel.getComponent(index);
                if (index == 3) return orientDot.getW();
                if (index == 4) return orientDot.getX();
                if (index == 5) return orientDot.getY();
                if (index == 6) return orientDot.getZ();
                if (index < 10) return forces.getComponent(index - 7);
                return torques.getComponent(index - 10);
            }
        }

        public int getDimension() {
            return 13;
        }

        public State mult(double scalar) {
            if (!rateOfChange) {
                return new State(pos.mult(scalar), new Quaternion(scalar*orient.getW(),
                        scalar*orient.getX(), scalar*orient.getY(), scalar*orient.getZ()),
                        mom.mult(scalar), angmom.mult(scalar), rateOfChange);
            } else {
                return new State(vel.mult(scalar), new Quaternion(scalar*orientDot.getW(),
                        scalar*orientDot.getX(), scalar*orientDot.getY(), scalar*orientDot.getZ()),
                        forces.mult(scalar), torques.mult(scalar), rateOfChange);
            }
        }

        public double mult(Vector v) {
            throw new UnsupportedOperationException();
        }

        public State multComponents(Vector v) {
            throw new UnsupportedOperationException();
        }

        public State subtract(Vector v) {
            return add(v.mult(-1.0));
        }

        public void toDoubleArray(double[] array, int offset) {
            for (int i=0; i<getDimension(); i++) array[offset+i] = getComponent(i);
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
    
    
    private class MassInertia extends MatrixImpl {
        private State state;
        private Matrix inv;
        
        private MassInertia(State state) {
            super(null);
            this.state = state;
            inv = new InverseMassInertia(this);
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
            return getInertia(state).getComponent(row - 3, column - 3);
        }

        public Matrix inverse() {
            return inv;
        }        
    }
    
    
    private class InverseMassInertia extends MatrixImpl {
        private State state;
        private Matrix inv;

        private InverseMassInertia(MassInertia mi) {
            super(null);
            this.state = mi.state;
            this.inv = mi;
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
            return getInvInertia(state).getComponent(row - 3, column - 3);
        }

        public Matrix inverse() {
            return inv;
        }        
    }
}

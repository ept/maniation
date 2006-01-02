package de.kleppmann.maniation.dynamics;

import java.util.List;

import de.kleppmann.maniation.maths.ConjugateGradient;
import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.ODE;
import de.kleppmann.maniation.maths.SparseMatrix;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.VectorImpl;

public class ConstrainedRigidBodies implements ODE, DynamicScene {

    private List<RigidBody> bodies = new java.util.ArrayList<RigidBody>();
    private List<Constraint> constraints = new java.util.ArrayList<Constraint>();
    private InertiaTensor inertia;
    private ForceVector forces;
    private VelocityVector velocities;
    private ConstraintCombination allConstraints;
    
    public ConstrainedRigidBodies() {
    }
    
    public void addBody(RigidBody body) {
        bodies.add(body);
    }
    
    public List<RigidBody> getBodies() {
        return bodies;
    }

    public void addConstraint(Constraint constr) {
        constraints.add(constr);
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public Vector getInitial() {
        inertia = new InertiaTensor(this);
        forces = new ForceVector();
        velocities = new VelocityVector();
        allConstraints = new ConstraintCombination(this);
        StateVector init = new StateVector(this);
        init.setDerivative(false);
        return init;
    }

    public Vector derivative(double time, Vector state) {
        if (!(state instanceof StateVector)) throw new IllegalArgumentException();
        StateVector s = (StateVector) state;
        s.updateBodies();
        //System.out.println("State: " + s);
        //System.out.println("Energy: " + bodies.get(0).getEnergy());
        allConstraints.update();
        //System.out.println("Deriv: " + s.getDerivative());
        return s.getDerivative().add(new ConstraintForces());
    }
    

    private class ForceVector extends VectorImpl {
        public ForceVector() {
            super(null);
        }
        
        public int getDimension() {
            return 6*bodies.size();
        }

        public double getComponent(int index) {
            RigidBody body = bodies.get(index/6);
            int n = index % 6;
            if (n < 3) return body.getForces().getComponent(n);
            return body.getTorques().getComponent(n - 3);
        }
    }


    private class VelocityVector extends VectorImpl {
        public VelocityVector() {
            super(null);
        }
        
        public int getDimension() {
            return 6*bodies.size();
        }

        public double getComponent(int index) {
            RigidBody body = bodies.get(index/6);
            int n = index % 6;
            if (n < 3) return body.getCoMVelocity().getComponent(n);
            return body.getAngularVelocity().getComponent(n - 3);
        }
    }
    
    
    private class ConstraintForces extends StateVector {
        
        private Vector constForce;

        public ConstraintForces() {
            super(ConstrainedRigidBodies.this);
            Vector term1 = allConstraints.getJacobianDot().mult(velocities);
            Vector term2 = allConstraints.getJacobian().mult(inertia.inverse().mult(forces));
            Vector rhs = term1.add(term2).add(allConstraints.getPenalty()).add(
                    allConstraints.getPenaltyDot()).mult(-1.0);
            Matrix[] lhs = new Matrix[3];
            lhs[0] = allConstraints.getJacobian();
            lhs[1] = inertia.inverse();
            lhs[2] = allConstraints.getJacobian().transpose();
            Vector lambda = (new ConjugateGradient(lhs, rhs)).solve();
            if (allConstraints.getJacobian().getRows() == lambda.getDimension()) {
                constForce = allConstraints.getJacobian().transpose().mult(lambda);
            } else {
                SparseMatrix.Slice[] slices = new SparseMatrix.Slice[1];
                slices[0] = new SparseMatrix.SliceImpl(allConstraints.getJacobian().transpose(), 0, 0);
                SparseMatrix jac = new SparseMatrix(allConstraints.getJacobian().getColumns(),
                        lambda.getDimension(), slices);
                constForce = jac.mult(lambda);
            }
        }

        public double getComponent(int index) {
            int mod = (index % 13) - 7, div = index / 13;
            if (mod < 0) return 0.0;
            return constForce.getComponent(6*div + mod);
        }

        public boolean isDerivative() {
            return true;
        }
    }
}

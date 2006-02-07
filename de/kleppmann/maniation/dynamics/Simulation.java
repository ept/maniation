package de.kleppmann.maniation.dynamics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import de.kleppmann.maniation.maths.ConjugateGradient;
import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.ODE;
import de.kleppmann.maniation.maths.ODEBacktrackException;
import de.kleppmann.maniation.maths.ODESolver;
import de.kleppmann.maniation.maths.RungeKutta;
import de.kleppmann.maniation.maths.SparseMatrix;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.VectorImpl;

public class Simulation {
    
    public static final double RESTING_TOLERANCE = 1e-4;
    public static final double PENETRATION_TOLERANCE = 1e-4;
    public static final double ELASTICITY = 0.9;
    
    private World world = new World();
    private List<Body> bodies = new java.util.ArrayList<Body>();
    private StateVector state, stateDot;
    private List<String> log = new java.util.ArrayList<String>();
    
    public void addBody(Body body) {
        bodies.add(body);
        Body[] array = new Body[bodies.size()];
        array = bodies.toArray(array);
        state = new StateVector(array, false);
        stateDot = new StateVector(array, true);
    }
    
    public World getWorld() {
        return world;
    }
    
    private void setTime(double time) {
        for (Body body : bodies) body.setSimulationTime(time);
    }
    
    public double totalEnergy() {
        double result = 0.0;
        for (Body b : bodies) result += b.getEnergy();
        return result;
    }
    
    public void run(double time) {
        ODESolver solver = new RungeKutta(new DifferentialEquation(), 0.02);
        log.add(state.toString());
        solver.solve(0.0, time);
        try {
            FileWriter writer = new FileWriter("/home/martin/graphics/maniation/matlab/javadata");
            writer.write("# name: data\n");
            writer.write("# type: matrix\n");
            writer.write("# rows: " + log.size() + "\n");
            writer.write("# columns: " + state.getDimension() + "\n");
            for (String line : log) writer.write(line + "\n");
            writer.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    private void interactions(double time, boolean allowBacktrack) throws ODEBacktrackException {
        // Determine all interactions between bodies
        InteractionList il = new InteractionList();
        for (int i=bodies.size()-1; i>=0; i--) {
            Body b = bodies.get(i);
            b.interaction(world, il, true);
            for (int j=bodies.size()-1; j>i; j--) b.interaction(bodies.get(j), il, true);
        }
        il.applyNonConstraints();
        // Compute collision impulses
        //constraintImpulses(il, time, allowBacktrack);
        // Compute resting contact forces
        constraintForces(il);
    }
    
    private void checkPenetration(InteractionList il, double time, boolean allowBacktrack)
            throws ODEBacktrackException {
        // Check for colliding contacts and abort this simulation step if necessary
        if ((il.getCollidingContacts().size() > 0) && allowBacktrack) {
            double penetrationTime = 0.0; boolean penetrated = false;
            for (Constraint c : il.getCollidingContacts()) {
                for (int i=0; i<c.getDimension(); i++) {
                    double d = c.getPenaltyDot().getComponent(i);
                    if (d < -PENETRATION_TOLERANCE) {
                        double t = c.getPenalty().getComponent(i) / d;
                        if ((!penetrated) || (t > penetrationTime)) penetrationTime = t;
                        penetrated = true;
                    }
                }
            }
            if (penetrated) throw new ODEBacktrackException(time - penetrationTime);
        }
    }
    
    private void constraintImpulses(InteractionList il, double time, boolean allowBacktrack)
            throws ODEBacktrackException {
        while (true) {
            // Repeat until there are no more colliding contacts
            il.classifyConstraints();
            checkPenetration(il, time, allowBacktrack);
            if (il.getCollidingContacts().size() == 0) break;
            // Set up elasticity vector (conceptually a diagonal matrix)
            Set<Constraint> constrs = new java.util.HashSet<Constraint>();
            constrs.addAll(il.getEqualityConstraints());
            constrs.addAll(il.getCollidingContacts());
            il.compileConstraints(constrs);
            double[] el = new double[il.getPenalty().getDimension()];
            for (int i=0; i<el.length; i++) el[i] = 1.0;
            for (Constraint c : il.getCollidingContacts()) {
                int offset = il.getConstraintOffset(c);
                for (int i=0; i<c.getDimension(); i++) el[i+offset] = 1.0 + ELASTICITY;
            }
            // Set up Lagrange multiplier equation and solve it
            Vector rhs = (new VectorImpl(el)).multComponents(
                    il.getJacobian().mult(il.getVelocity())).mult(-1.0);
            Matrix[] lhs = new Matrix[3];
            lhs[0] = il.getJacobian();
            lhs[1] = il.getMassInertia().inverse();
            lhs[2] = il.getJacobian().transpose();
            Vector lambda = (new ConjugateGradient(lhs, rhs)).solve();
            // Compute constraint impulses from Lagrange multipliers, and apply them to the system
            Vector impulse;
            if (il.getJacobian().getRows() == lambda.getDimension()) {
                impulse = il.getJacobian().transpose().mult(lambda);
            } else {
                SparseMatrix.Slice[] slices = new SparseMatrix.Slice[1];
                slices[0] = new SparseMatrix.SliceImpl(il.getJacobian().transpose(), 0, 0);
                SparseMatrix jac = new SparseMatrix(il.getJacobian().getColumns(),
                        lambda.getDimension(), slices);
                impulse = jac.mult(lambda);
            }
            il.applyImpulses(impulse);
        }
    }
    
    private void constraintForces(InteractionList il) {
        il.classifyConstraints();
        Vector lambda;
        Set<Constraint> constrs = new java.util.HashSet<Constraint>();
        Set<Constraint> contacts = new java.util.HashSet<Constraint>();
        constrs.addAll(il.getEqualityConstraints());
        constrs.addAll(il.getRestingContacts());
        contacts.addAll(il.getRestingContacts());
        boolean lambdaNegative;
        do {
            il.compileConstraints(constrs);
            // Set up Lagrange multiplier equation and solve it
            Vector term1 = il.getJacobianDot().mult(il.getVelocity());
            Vector term2 = il.getJacobian().mult(il.getAcceleration());
            Vector rhs = term1.add(term2)/*.add(il.getPenalty()).add(il.getPenaltyDot())*/.mult(-1.0);
            Matrix[] lhs = new Matrix[3];
            lhs[0] = il.getJacobian();
            lhs[1] = il.getMassInertia().inverse();
            lhs[2] = il.getJacobian().transpose();
            lambda = (new ConjugateGradient(lhs, rhs)).solve();
            // Determine all Lagrange multipliers which are negative at resting contacts,
            // and remove them from the active set of constraints
            lambdaNegative = false;
            for (Constraint c : contacts) {
                int offset = il.getConstraintOffset(c);
                boolean thisNegative = false;
                for (int i=0; i<c.getDimension(); i++)
                    if (lambda.getComponent(offset+i) < 0.0) thisNegative = true;
                if (thisNegative) {
                    constrs.remove(c); contacts.remove(c);
                    lambdaNegative = true;
                }
            }
        } while (lambdaNegative);
        // Compute constraint forces from Lagrange multipliers, and apply them to the system
        Vector constForce;
        if (il.getJacobian().getRows() == lambda.getDimension()) {
            constForce = il.getJacobian().transpose().mult(lambda);
        } else {
            SparseMatrix.Slice[] slices = new SparseMatrix.Slice[1];
            slices[0] = new SparseMatrix.SliceImpl(il.getJacobian().transpose(), 0, 0);
            SparseMatrix jac = new SparseMatrix(il.getJacobian().getColumns(),
                    lambda.getDimension(), slices);
            constForce = jac.mult(lambda);
        }
        il.applyForces(constForce);
    }
    
    
    private class DifferentialEquation implements ODE {
        public Vector derivative(double time, Vector state, boolean allowBacktrack)
                throws ODEBacktrackException {
            if (!(state instanceof StateVector)) throw new IllegalArgumentException();
            setTime(time);
            ((StateVector) state).apply();
            interactions(time, allowBacktrack);
            return stateDot;
        }

        public Vector getInitial() {
            return state;
        }

        public void timeStepCompleted(double time, Vector state) {
            log.add(state.toString());
            System.out.println("Total energy: " + totalEnergy());
        }
    }
}

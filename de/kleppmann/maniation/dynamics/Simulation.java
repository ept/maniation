package de.kleppmann.maniation.dynamics;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

import de.kleppmann.maniation.maths.ConjugateGradient;
import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.ODE;
import de.kleppmann.maniation.maths.ODEBacktrackException;
import de.kleppmann.maniation.maths.RungeKutta;
import de.kleppmann.maniation.maths.SparseMatrix;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.VectorImpl;

public class Simulation {
    
    public static final double RESTING_TOLERANCE = 1e-3;
    public static final double PENETRATION_TOLERANCE = 1e-3;
    public static final double ELASTICITY = 0.2;
    public static final double FRAMES_PER_SECOND = 120.0;
    
    private World world = new World();
    private SimulationObject.State worldState = world.getInitialState();
    private List<GeneralizedBody> bodies = new java.util.ArrayList<GeneralizedBody>();
    private CompoundBody compoundBody;
    private List<String> log = new java.util.ArrayList<String>();
    
    public void addBody(GeneralizedBody body) {
        bodies.add(body);
    }
    
    public World getWorld() {
        return world;
    }
    
    public void run(double time) {
        // Set up compound body
        compoundBody = new CompoundBody(bodies.toArray(new GeneralizedBody[bodies.size()]));
        GeneralizedBody.State initialState = compoundBody.getInitialState();
        log.clear();
        log.add("0.0 " + initialState.toString());
        // Initialize ODE solver
        RungeKutta solver = new RungeKutta(new DifferentialEquation(), 1.0/FRAMES_PER_SECOND);
        solver.setMaxTimeStep(1.0/FRAMES_PER_SECOND);
        // Run simulation
        solver.solve(0.0, time);
        // Write results to file
        try {
            FileWriter writer = new FileWriter("/home/martin/graphics/maniation/matlab/javadata");
            writer.write("# name: data\n");
            writer.write("# type: matrix\n");
            writer.write("# rows: " + log.size() + "\n");
            writer.write("# columns: " + (initialState.getDimension() + 1) + "\n");
            for (String line : log) writer.write(line + "\n");
            writer.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    private void checkPenetration(StateVector state, InteractionList il, double time)
            throws ODEBacktrackException {
        // Check for colliding contacts and abort this simulation step if necessary
        il.classifyConstraints(state);
        if (il.getCollidingContacts().size() > 0) {
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
            if (penetrated) throw new ODEBacktrackException(time, time - penetrationTime);
        }
    }
    
    private StateVector constraintImpulses(StateVector state, InteractionList il) {
        while (true) {
            // Repeat until there are no more colliding contacts
            il.classifyConstraints(state);
            if (il.getCollidingContacts().size() == 0) break;
            System.out.print("*");
            // Set up elasticity vector (conceptually a diagonal matrix)
            Set<Constraint> constrs = new java.util.HashSet<Constraint>();
            constrs.addAll(il.getEqualityConstraints());
            constrs.addAll(il.getCollidingContacts());
            il.compileConstraints(state, constrs);
            double[] el = new double[il.getPenalty().getDimension()];
            for (int i=0; i<el.length; i++) el[i] = 1.0;
            for (Constraint c : il.getCollidingContacts()) {
                int offset = il.getConstraintOffset(c);
                for (int i=0; i<c.getDimension(); i++) el[i+offset] = 1.0 + ELASTICITY;
            }
            // Set up Lagrange multiplier equation and solve it
            Vector rhs = (new VectorImpl(el)).multComponents(
                    il.getJacobian().mult(state.getVelocities())).mult(-1.0);
            Matrix[] lhs = new Matrix[3];
            lhs[0] = il.getJacobian();
            lhs[1] = state.getMassInertia().inverse();
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
            state = state.applyImpulse(impulse);
        }
        return state;
    }
    
    private StateVector constraintForces(StateVector state, InteractionList il) {
        il.classifyConstraints(state);
        Vector lambda;
        Set<Constraint> constrs = new java.util.HashSet<Constraint>();
        Set<Constraint> contacts = new java.util.HashSet<Constraint>();
        constrs.addAll(il.getEqualityConstraints());
        constrs.addAll(il.getRestingContacts());
        contacts.addAll(il.getRestingContacts());
        boolean lambdaNegative;
        do {
            il.compileConstraints(state, constrs);
            // If there are no constraints, do nothing
            if (il.getJacobian().getRows() == 0) return state;
            // Set up Lagrange multiplier equation and solve it
            Vector term1 = il.getJacobianDot().mult(state.getVelocities());
            Vector term2 = il.getJacobian().mult(state.getAccelerations());
            Vector rhs = term1.add(term2)/*.add(il.getPenalty()).add(il.getPenaltyDot())*/.mult(-1.0);
            Matrix[] lhs = new Matrix[3];
            lhs[0] = il.getJacobian();
            lhs[1] = state.getMassInertia().inverse();
            lhs[2] = il.getJacobian().transpose();
            lambda = (new ConjugateGradient(lhs, rhs)).solve();
            // Determine all Lagrange multipliers which are negative at resting contacts,
            // and remove them from the active set of constraints
            lambdaNegative = false;
            Set<Constraint> glue = new java.util.HashSet<Constraint>();
            for (Constraint c : contacts) {
                int offset = il.getConstraintOffset(c);
                boolean thisNegative = false;
                for (int i=0; i<c.getDimension(); i++)
                    if (lambda.getComponent(offset+i) < 0.0) thisNegative = true;
                if (thisNegative) {
                    glue.add(c);
                    lambdaNegative = true;
                }
            }
            constrs.removeAll(glue);
            contacts.removeAll(glue);
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
        return state.applyForce(constForce);
    }
    
    
    private class DifferentialEquation implements ODE {
        
        Vector lastCompleted = null;
        double lastCompletedTime, lastAddedTime = -1e20;
        int lastAddedFrame;
        
        public Vector derivative(double time, Vector state, boolean allowBacktrack)
                throws ODEBacktrackException {
            if (!(state instanceof StateVector)) throw new IllegalArgumentException();
            StateVector sv = (StateVector) state;
            InteractionList il = new InteractionList();
            compoundBody.interaction(sv, worldState, il, true);
            // Check if penetration has occurred -- may throw ODEBacktrackException
            if (allowBacktrack) checkPenetration(sv, il, time);
            // Compute constraint/resting contact forces
            for (Interaction i : il.getNonConstraints()) sv = sv.handleInteraction(i);
            sv = constraintForces(sv, il);
            return sv.getDerivative();
        }

        public Vector impulse(double time, Vector state) {
            if (!(state instanceof StateVector)) throw new IllegalArgumentException();
            StateVector sv = (StateVector) state;
            InteractionList il = new InteractionList();
            compoundBody.interaction(sv, worldState, il, true);
            return constraintImpulses(sv, il);
        }

        public Vector getInitial() {
            return compoundBody.getInitialState();
        }

        public void timeStepCompleted(double time, Vector state) {
            if (time - lastAddedTime < 1.0/FRAMES_PER_SECOND) {
                lastCompleted = state;
                lastCompletedTime = time;
                return;
            }
            DecimalFormat format = new DecimalFormat("######0.000000000000000");
            if (lastCompleted != null) {
                while (lastAddedTime + 1.0/FRAMES_PER_SECOND < time) {
                    lastAddedFrame++;
                    lastAddedTime = 1.0*lastAddedFrame/FRAMES_PER_SECOND;
                    double dt = time - lastCompletedTime;
                    Vector interpolated = lastCompleted.mult((time - lastAddedTime) / dt).add(
                            state.mult((lastAddedTime - lastCompletedTime) / dt));
                    log.add(format.format(lastAddedTime) + " " + interpolated.toString());
                }
            } else {
                lastAddedFrame = (int) Math.floor(time*FRAMES_PER_SECOND);
                lastAddedTime = 1.0*lastAddedFrame/FRAMES_PER_SECOND;
                log.add(format.format(lastAddedTime) + " " + state.toString());
            }
            lastCompleted = state;
            lastCompletedTime = time;
            //System.out.println("Total energy: " + compoundBody.getEnergy());
        }
    }
}

package de.kleppmann.maniation.dynamics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import de.kleppmann.maniation.maths.ConjugateGradient;
import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.ODE;
import de.kleppmann.maniation.maths.ODESolver;
import de.kleppmann.maniation.maths.RungeKutta;
import de.kleppmann.maniation.maths.SparseMatrix;
import de.kleppmann.maniation.maths.Vector;

public class Simulation {
    
    private World world = new World();
    private List<Body> bodies = new java.util.ArrayList<Body>();
    private StateVector state = new StateVector(null, false), stateDot = new StateVector(null, true);
    private List<String> log = new java.util.ArrayList<String>();
    
    public void addBody(Body body) {
        bodies.add(body);
        Body[] array = new Body[bodies.size()];
        array = bodies.toArray(array);
        state = new StateVector(array, false);
        stateDot = new StateVector(array, true);
    }
    
    private void setTime(double time) {
        for (Body body : bodies) body.setSimulationTime(time);
    }
    
    public void run(double time) {
        ODESolver solver = new RungeKutta(new DifferentialEquation(), 0.1);
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
    
    private void interactions() {
        InteractionList ias = new InteractionList();
        for (int i=bodies.size()-1; i>=0; i--) {
            Body b = bodies.get(i);
            b.interaction(world, ias, true);
            for (int j=bodies.size(); j>i; j--) b.interaction(bodies.get(j), ias, true);
        }
        ias.assemble();
        constraintForces(ias);
    }
    
    private void constraintForces(InteractionList il) {
        Vector term1 = il.getJacobianDot().mult(il.getVelocity());
        Vector term2 = il.getJacobian().mult(il.getAcceleration());
        Vector rhs = term1.add(term2).add(il.getPenalty()).add(il.getPenaltyDot()).mult(-1.0);
        Matrix[] lhs = new Matrix[3];
        lhs[0] = il.getJacobian();
        lhs[1] = il.getMassInertia().inverse();
        lhs[2] = il.getJacobian().transpose();
        Vector lambda = (new ConjugateGradient(lhs, rhs)).solve();
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
        public Vector derivative(double time, Vector state) {
            if (!(state instanceof StateVector)) throw new IllegalArgumentException();
            setTime(time);
            ((StateVector) state).apply();
            interactions();
            return stateDot;
        }

        public Vector getInitial() {
            return state;
        }

        public void timeStepCompleted(double time, Vector state) {
            log.add(state.toString());
        }
    }
}

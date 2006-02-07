package de.kleppmann.maniation.dynamics;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.SparseMatrix;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.VectorImpl;
import de.kleppmann.maniation.maths.SparseMatrix.Slice;

public class InteractionList {
    
    private Set<Constraint> constraints = new java.util.HashSet<Constraint>();
    private Set<Interaction> other = new java.util.HashSet<Interaction>();
    private Vector veloc, accel, penalty, penaltyDot;
    private SparseMatrix massInertia, jacobian, jacobianDot;
    private Set<Constraint> equalities, colliding, resting;
    private Map<Body,Integer> bodyOffsets;
    private Map<Constraint,Integer> constrOffsets;
    
    public void addInteraction(Interaction ia) {
        if (ia instanceof Constraint) constraints.add((Constraint) ia);
        else other.add(ia);
    }
    
    public void applyNonConstraints() {
        for (Interaction i : other)
            for (SimulationObject obj : i.getObjects()) obj.handleInteraction(i);
    }
    
    public void classifyConstraints() {
        equalities = new java.util.HashSet<Constraint>();
        colliding = new java.util.HashSet<Constraint>();
        resting = new java.util.HashSet<Constraint>();
        for (Constraint c : constraints) {
            // If it's an inequality, is the contact colliding, resting or separating?
            if (c.isInequality()) {
                boolean isColliding = false, isSeparating = true;
                for (int i=0; i<c.getDimension(); i++) {
                    double component = c.getPenaltyDot().getComponent(i);
                    if (component < -Simulation.RESTING_TOLERANCE) isColliding = true;
                    if (component <  Simulation.RESTING_TOLERANCE) isSeparating = false;
                }
                if (isColliding) colliding.add(c); else
                if (!isSeparating) resting.add(c); 
            } else equalities.add(c);
        }
    }
    
    public void compileConstraints(Collection<Constraint> constraintList) {
        // Put all bodies mentioned by the constraints in some order, and assign each
        // an offset into the velocity/acceleration/force vectors.
        int bodyOffset = 0, bodyCount = 0;
        bodyOffsets = new java.util.HashMap<Body,Integer>();
        for (Constraint constr : constraintList)
            for (SimulationObject obj : constr.getObjects())
                if (obj instanceof Body) {
                    Body b = (Body) obj;
                    if (bodyOffsets.get(b) == null) {
                        bodyOffsets.put(b, bodyOffset);
                        bodyOffset += b.getVelocities().getDimension();
                        bodyCount++;
                    }
                }
        // Assemble the velocity and acceleration vectors, and the mass/inertia matrix
        // containing all bodies.
        double[] v = new double[bodyOffset], a = new double[bodyOffset];
        SparseMatrix.Slice[] mass = new SparseMatrix.Slice[bodyCount];
        int j = 0;
        for (Map.Entry<Body,Integer> entry : bodyOffsets.entrySet()) {
            int offset = entry.getValue();
            entry.getKey().getVelocities().toDoubleArray(v, offset);
            entry.getKey().getAccelerations().toDoubleArray(a, offset);
            mass[j] = new SparseMatrix.SliceImpl(entry.getKey().getMassInertia(), offset, offset);
            j++;
        }
        veloc = new VectorImpl(v); accel = new VectorImpl(a);
        massInertia = new SparseMatrix(v.length, v.length, mass);
        // Assign each constraint to some offset in the constraint vector
        int constrOffset = 0;
        constrOffsets = new java.util.HashMap<Constraint,Integer>();
        for (Constraint c : constraintList) {
            if (constrOffsets.get(c) == null) {
                constrOffsets.put(c, constrOffset);
                constrOffset += c.getDimension();                
            }
        }
        List<JacobianSlice> jSlices = new java.util.ArrayList<JacobianSlice>();
        List<JacobianSlice> jdotSlices = new java.util.ArrayList<JacobianSlice>();
        double[] p = new double[constrOffset], pdot = new double[constrOffset];
        // Build the Jacobian matrices and the penalty vectors
        for (Map.Entry<Constraint,Integer> entry : constrOffsets.entrySet()) {
            Constraint c = entry.getKey();
            int offset = entry.getValue();
            c.getPenalty().toDoubleArray(p, offset);
            c.getPenaltyDot().toDoubleArray(pdot, offset);
            for (Body b : c.getJacobian().keySet())
                jSlices.add(new JacobianSlice(c, offset, b, bodyOffsets.get(b).intValue(), false));
            for (Body b : c.getJacobianDot().keySet())
                jdotSlices.add(new JacobianSlice(c, offset, b, bodyOffsets.get(b).intValue(), true));
        }
        // Create the matrix and vector objects
        penalty = new VectorImpl(p);
        penaltyDot = new VectorImpl(pdot);
        jacobian = new SparseMatrix(constrOffset, veloc.getDimension(),
                jSlices.toArray(new SparseMatrix.Slice[jSlices.size()]));
        jacobianDot = new SparseMatrix(constrOffset, veloc.getDimension(),
                jdotSlices.toArray(new SparseMatrix.Slice[jdotSlices.size()]));
    }

    public void applyForces(Vector force) {
        for (Map.Entry<Body,Integer> entry : bodyOffsets.entrySet()) {
            Body body = entry.getKey();
            int offset = entry.getValue();
            int length = body.getVelocities().getDimension();
            double[] v = new double[length];
            for (int i=0; i<length; i++) v[i] = force.getComponent(i+offset);
            body.applyForce(new VectorImpl(v));
        }
    }
    
    public void applyImpulses(Vector impulse) {
        for (Map.Entry<Body,Integer> entry : bodyOffsets.entrySet()) {
            Body body = entry.getKey();
            int offset = entry.getValue();
            int length = body.getVelocities().getDimension();
            double[] v = new double[length];
            for (int i=0; i<length; i++) v[i] = impulse.getComponent(i+offset);
            body.applyImpulse(new VectorImpl(v));
        }
    }
    
    public Vector getVelocity() { return veloc; }
    public Vector getAcceleration() { return accel; }
    public Vector getPenalty() { return penalty; }
    public Vector getPenaltyDot() { return penaltyDot; }
    public SparseMatrix getMassInertia() { return massInertia; }
    public SparseMatrix getJacobian() { return jacobian; }
    public SparseMatrix getJacobianDot() { return jacobianDot; }
    public Set<Constraint> getEqualityConstraints() { return equalities; }
    public Set<Constraint> getCollidingContacts() { return colliding; }
    public Set<Constraint> getRestingContacts() { return resting; }
    
    public int getBodyOffset(Body b) {
        return bodyOffsets.get(b);
    }
    
    public int getConstraintOffset(Constraint c) {
        return constrOffsets.get(c);
    }


    private class JacobianSlice implements Slice {

        private Constraint constr;
        private Body body;
        private int constrOffset, bodyOffset;
        private boolean dot;
        
        public JacobianSlice(Constraint constr, int constrOffset,
                Body body, int bodyOffset, boolean dot) {
            this.constr = constr;
            this.constrOffset = constrOffset;
            this.body = body;
            this.bodyOffset = bodyOffset;
            this.dot = dot;
        }

        public int getStartRow() {
            return constrOffset;
        }

        public int getStartColumn() {
            return bodyOffset;
        }

        public Matrix getMatrix() {
            if (dot) return constr.getJacobianDot().get(body); 
            else return constr.getJacobian().get(body);
        }
    }
}

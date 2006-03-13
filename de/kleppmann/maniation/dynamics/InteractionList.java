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
    private Set<Constraint> equalities;
    private Set<InequalityConstraint> colliding, resting;
    private Map<GeneralizedBody, Integer> bodyOffsets, bodyStateSizes;
    private Map<Constraint, Integer> constrOffsets;
    
    public void addInteraction(Interaction ia) {
        if (ia instanceof Constraint) constraints.add((Constraint) ia);
        else other.add(ia);
    }
    
    public void classifyConstraints(StateVector state) {
        equalities = new java.util.HashSet<Constraint>();
        colliding = new java.util.HashSet<InequalityConstraint>();
        resting = new java.util.HashSet<InequalityConstraint>();
        for (Constraint c : constraints) {
            c.setStateMapping(state.getStateMap());
            // If it's an inequality, is the contact colliding, resting or separating?
            if ((c instanceof InequalityConstraint) && (((InequalityConstraint) c).isInequality())) {
                boolean isColliding = false, isSeparating = true;
                for (int i=0; i<c.getDimension(); i++) {
                    double component = c.getPenaltyDot().getComponent(i);
                    if (component < -Simulation.RESTING_TOLERANCE) isColliding = true;
                    if (component <  Simulation.RESTING_TOLERANCE) isSeparating = false;
                }
                if (isColliding) colliding.add((InequalityConstraint) c); else
                if (!isSeparating) resting.add((InequalityConstraint) c); 
            } else equalities.add(c);
        }
    }
    
    public void compileConstraints(StateVector state, Collection<Constraint> constraintList) {
        // Put all bodies mentioned by the constraints in some order, and assign each
        // an offset into the velocity/acceleration/force vectors.
        int bodyOffset = 0, bodyCount = 0;
        bodyOffsets = new java.util.HashMap<GeneralizedBody, Integer>();
        bodyStateSizes = new java.util.HashMap<GeneralizedBody, Integer>();
        for (Constraint constr : constraintList) {
            constr.setStateMapping(state.getStateMap());
            for (SimulationObject obj : constr.getObjects()) {
                if (obj instanceof GeneralizedBody) {
                    GeneralizedBody b = (GeneralizedBody) obj;
                    if (bodyOffsets.get(b) == null) {
                        GeneralizedBody.State bstate = state.getStateMap().get(b);
                        int bsize = bstate.getVelocities().getDimension();
                        bodyOffsets.put(b, bodyOffset);
                        bodyStateSizes.put(b, bsize);
                        bodyOffset += bsize;
                        bodyCount++;
                    }
                }
            }
        }
        // Assemble the velocity and acceleration vectors, and the mass/inertia matrix
        // containing all bodies.
        double[] v = new double[bodyOffset], a = new double[bodyOffset];
        SparseMatrix.Slice[] mass = new SparseMatrix.Slice[bodyCount];
        int j = 0;
        for (Map.Entry<GeneralizedBody,Integer> entry : bodyOffsets.entrySet()) {
            GeneralizedBody.State bstate = state.getStateMap().get(entry.getKey());
            int offset = entry.getValue();
            bstate.getVelocities().toDoubleArray(v, offset);
            bstate.getAccelerations().toDoubleArray(a, offset);
            mass[j] = new SparseMatrix.SliceImpl(bstate.getMassInertia(), offset, offset);
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
            for (GeneralizedBody b : c.getJacobian().keySet())
                jSlices.add(new JacobianSlice(c, offset, b, bodyOffsets.get(b).intValue(), false));
            for (GeneralizedBody b : c.getJacobianDot().keySet())
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

    public StateVector applyForces(StateVector state, Vector force, boolean impulse) {
        Map<GeneralizedBody, Vector> forceMap = new java.util.HashMap<GeneralizedBody, Vector>();
        for (Map.Entry<GeneralizedBody,Integer> entry : bodyOffsets.entrySet()) {
            GeneralizedBody body = entry.getKey();
            int offset = entry.getValue();
            int length = bodyStateSizes.get(body);
            double[] v = new double[length];
            for (int i=0; i<length; i++) v[i] = force.getComponent(i+offset);
            forceMap.put(body, new VectorImpl(v));
        }
        return state.applyForces(forceMap, impulse);
    }
    
    public Vector getVelocity() { return veloc; }
    public Vector getAcceleration() { return accel; }
    public Vector getPenalty() { return penalty; }
    public Vector getPenaltyDot() { return penaltyDot; }
    public SparseMatrix getMassInertia() { return massInertia; }
    public SparseMatrix getJacobian() { return jacobian; }
    public SparseMatrix getJacobianDot() { return jacobianDot; }
    public Set<Constraint> getEqualityConstraints() { return equalities; }
    public Set<InequalityConstraint> getCollidingContacts() { return colliding; }
    public Set<InequalityConstraint> getRestingContacts() { return resting; }
    
    public int getBodyOffset(GeneralizedBody b) {
        return bodyOffsets.get(b);
    }
    
    public int getConstraintOffset(Constraint c) {
        return constrOffsets.get(c);
    }


    private class JacobianSlice implements Slice {

        private Constraint constr;
        private GeneralizedBody body;
        private int constrOffset, bodyOffset;
        private boolean dot;
        
        public JacobianSlice(Constraint constr, int constrOffset,
                GeneralizedBody body, int bodyOffset, boolean dot) {
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

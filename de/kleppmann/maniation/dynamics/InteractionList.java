package de.kleppmann.maniation.dynamics;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.SparseMatrix;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.VectorImpl;

public class InteractionList {
    
    private Set<Constraint> constraints = new java.util.HashSet<Constraint>();
    private Set<Interaction> other = new java.util.HashSet<Interaction>();
    private Set<Constraint> equalities;
    private Set<InequalityConstraint> colliding, resting;
    private Map<Constraint, Integer> constrOffsets;
    private Vector penalty, penaltyDot;
    private SparseMatrix jacobian, jacobianDot;
    
    public void addInteraction(Interaction ia) {
        if (ia instanceof Constraint) constraints.add((Constraint) ia);
        else other.add(ia);
    }
    
    public void classifyConstraints(StateVector state) {
        equalities = new java.util.HashSet<Constraint>();
        colliding = new java.util.HashSet<InequalityConstraint>();
        resting = new java.util.HashSet<InequalityConstraint>();
        Set<Constraint> notIgnored = new java.util.HashSet<Constraint>();
        for (Constraint c : constraints) {
            c.setStateMapping(state.getStateMap());
            // If it's an inequality...
            if ((c instanceof InequalityConstraint) && (((InequalityConstraint) c).isInequality())) {
                // Discard positive inequalities
                boolean positive = true;
                for (int i=0; i<c.getDimension(); i++)
                    if (c.getPenalty().getComponent(i) < 0 /*Simulation.PENETRATION_TOLERANCE*/)
                        positive = false;
                if (positive) continue;
                // Is the contact colliding, resting or separating?
                boolean isColliding = false, isSeparating = true;
                for (int i=0; i<c.getDimension(); i++) {
                    double component = c.getPenaltyDot().getComponent(i);
                    if (component < -Simulation.RESTING_TOLERANCE) isColliding = true;
                    if (component <  Simulation.RESTING_TOLERANCE) isSeparating = false;
                }
                if (isColliding) colliding.add((InequalityConstraint) c); else
                if (!isSeparating) resting.add((InequalityConstraint) c); 
            } else equalities.add(c);
            notIgnored.add(c); // does not get added if continue has been called
        }
        constraints = notIgnored;
    }
    
    public void compileConstraints(StateVector state, Collection<Constraint> constraintList) {
        // Update the state of all constraints.
        for (Constraint constr : constraintList) constr.setStateMapping(state.getStateMap());
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
            for (GeneralizedBody b : c.getJacobian().keySet()) {
                int bodyOffset = state.getOffsetMap().get(b);
                jSlices.add(new JacobianSlice(c, offset, b, bodyOffset, false));
            }
            for (GeneralizedBody b : c.getJacobianDot().keySet()) {
                int bodyOffset = state.getOffsetMap().get(b);
                jdotSlices.add(new JacobianSlice(c, offset, b, bodyOffset, true));
            }
        }
        // Create the matrix and vector objects
        int dimension = state.getVelocities().getDimension();
        penalty = new VectorImpl(p);
        penaltyDot = new VectorImpl(pdot);
        jacobian = new SparseMatrix(constrOffset, dimension,
                jSlices.toArray(new SparseMatrix.Slice[jSlices.size()]));
        jacobianDot = new SparseMatrix(constrOffset, dimension,
                jdotSlices.toArray(new SparseMatrix.Slice[jdotSlices.size()]));
    }

    public Vector getPenalty() { return penalty; }
    public Vector getPenaltyDot() { return penaltyDot; }
    public SparseMatrix getJacobian() { return jacobian; }
    public SparseMatrix getJacobianDot() { return jacobianDot; }
    public Set<Interaction> getNonConstraints() { return other; }
    public Set<Constraint> getAllConstraints() { return constraints; }
    public Set<Constraint> getEqualityConstraints() { return equalities; }
    public Set<InequalityConstraint> getCollidingContacts() { return colliding; }
    public Set<InequalityConstraint> getRestingContacts() { return resting; }
    
    public int getConstraintOffset(Constraint c) {
        return constrOffsets.get(c);
    }


    private class JacobianSlice implements SparseMatrix.Slice {

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
        
        @Override
        public String toString() {
            return getMatrix().toString();
        }
    }
}

package de.kleppmann.maniation.dynamics;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.SparseMatrix;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.VectorImpl;
import de.kleppmann.maniation.maths.SparseMatrix.Slice;

public class InteractionList {
    
    private List<Constraint> constraints = new java.util.ArrayList<Constraint>();
    private List<Interaction> other = new java.util.ArrayList<Interaction>();
    private Vector veloc, accel, penalty, penaltyDot;
    private SparseMatrix massInertia, jacobian, jacobianDot;
    private List<Constraint> collisions;
    private Map<Body,Integer> bodyOffsets;
    
    public void addInteraction(Interaction ia) {
        if (ia instanceof Constraint) constraints.add((Constraint) ia);
        else other.add(ia);
    }
    
    private void calcBodyOffsets() {
        Set<Body> bodies = new java.util.HashSet<Body>();
        for (Interaction i : constraints)
            for (SimulationObject obj : i.getObjects())
                if (obj instanceof Body) bodies.add((Body) obj);
        int i = 0;
        bodyOffsets = new java.util.HashMap<Body,Integer>();
        for (Body b : bodies) {
            bodyOffsets.put(b, i);
            i += b.getVelocities().getDimension();
        }
        double[] v = new double[i], a = new double[i];
        SparseMatrix.Slice[] mass = new SparseMatrix.Slice[bodies.size()];
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
    }
    
    public void assemble() {
        for (Interaction i : other)
            for (SimulationObject obj : i.getObjects()) obj.handleInteraction(i);
        calcBodyOffsets();
        int constr = 0;
        for (Constraint c : constraints) constr += c.getPenalty().getDimension();
        List<JacobianSlice> j = new java.util.ArrayList<JacobianSlice>();
        List<JacobianSlice> jdot = new java.util.ArrayList<JacobianSlice>();
        double[] p = new double[constr], pdot = new double[constr];
        int offset = 0;
        collisions = new java.util.ArrayList<Constraint>();
        for (Constraint c : constraints) {
            c.getPenalty().toDoubleArray(p, offset);
            c.getPenaltyDot().toDoubleArray(pdot, offset);
            if (isColliding(c)) collisions.add(c);
            for (Body b : c.getJacobian().keySet())
                j.add(new JacobianSlice(c, offset, b, bodyOffsets.get(b).intValue(), false));
            for (Body b : c.getJacobianDot().keySet())
                jdot.add(new JacobianSlice(c, offset, b, bodyOffsets.get(b).intValue(), true));
            offset += c.getPenalty().getDimension();
        }
        penalty = new VectorImpl(p);
        penaltyDot = new VectorImpl(pdot);
        jacobian = new SparseMatrix(constr, veloc.getDimension(),
                j.toArray(new SparseMatrix.Slice[j.size()]));
        jacobianDot = new SparseMatrix(constr, veloc.getDimension(),
                jdot.toArray(new SparseMatrix.Slice[jdot.size()]));
    }
    
    private boolean isColliding(Constraint constr) {
        if (!constr.isInequality()) return false;
        Vector cd = constr.getPenaltyDot();
        for (int i=0; i<cd.getDimension(); i++)
            if (cd.getComponent(i) < -1e-4) return true;
        return false;
    }
    
    public Vector getVelocity() { return veloc; }
    public Vector getAcceleration() { return accel; }
    public Vector getPenalty() { return penalty; }
    public Vector getPenaltyDot() { return penaltyDot; }
    public SparseMatrix getMassInertia() { return massInertia; }
    public SparseMatrix getJacobian() { return jacobian; }
    public SparseMatrix getJacobianDot() { return jacobianDot; }
    public List<Constraint> getConstraints() { return constraints; }
    public List<Constraint> getCollisions() { return collisions; }

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

package de.kleppmann.maniation.dynamics;

import java.util.List;
import java.util.Map;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.SparseMatrix;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.VectorImpl;
import de.kleppmann.maniation.maths.SparseMatrix.Slice;

public class ConstraintCombination {
    
    private DynamicScene scene;
    private SparseMatrix jacobian, jacobianDot;
    private Vector penalty, penaltyDot;
    private long currentUpdate = 0;
    private int constrCount;

    public ConstraintCombination(DynamicScene scene) {
        this.scene = scene;
        List<SparseMatrix.Slice> j    = new java.util.ArrayList<SparseMatrix.Slice>();
        List<SparseMatrix.Slice> jdot = new java.util.ArrayList<SparseMatrix.Slice>();
        constrCount = 0;
        for (Constraint constr : scene.getConstraints()) {
            for (Map.Entry<RigidBody,Matrix> entry : constr.getJacobian().entrySet()) {
                j.add(new JacobianSlice(constr, constrCount, entry.getKey(), false));
            }
            for (Map.Entry<RigidBody,Matrix> entry : constr.getJacobianDot().entrySet()) {
                jdot.add(new JacobianSlice(constr, constrCount, entry.getKey(), true));
            }
            constrCount += constr.getPenalty().getDimension();
        }
        jacobian = new SparseMatrix(constrCount, 6*scene.getBodies().size(),
                j.toArray(new SparseMatrix.Slice[j.size()]));
        jacobianDot = new SparseMatrix(constrCount, 6*scene.getBodies().size(),
                jdot.toArray(new SparseMatrix.Slice[jdot.size()]));
    }
    
    public void update() {
        currentUpdate++;
        double[] pv = new double[constrCount], pvdot = new double[constrCount];
        int offs = 0;
        for (Constraint constr : scene.getConstraints()) {
            Vector c = constr.getPenalty();
            Vector cdot = constr.getPenaltyDot();
            c.toDoubleArray(pv, offs);
            cdot.toDoubleArray(pvdot, offs);
            offs += c.getDimension();
        }
        penalty = new VectorImpl(pv);
        penaltyDot = new VectorImpl(pvdot);
    }

    public Vector getPenalty() {
        return penalty;
    }

    public Vector getPenaltyDot() {
        return penaltyDot;
    }

    public SparseMatrix getJacobian() {
        return jacobian;
    }

    public SparseMatrix getJacobianDot() {
        return jacobianDot;
    }
    
    
    private class JacobianSlice implements Slice {

        private Constraint constr;
        private int constrOffset;
        private RigidBody body;
        private int bodyNo;
        private boolean dot;
        private Matrix mat;
        private long lastUpdate = -1;
        
        public JacobianSlice(Constraint constr, int constrOffset,
                RigidBody body, boolean dot) {
            this.constr = constr;
            this.constrOffset = constrOffset;
            this.body = body;
            this.bodyNo = scene.getBodies().indexOf(body);
            this.dot = dot;
        }

        public int getStartRow() {
            return constrOffset;
        }

        public int getStartColumn() {
            return 6*bodyNo;
        }

        public Matrix getMatrix() {
            if (lastUpdate != currentUpdate) {
                if (dot) mat = constr.getJacobianDot().get(body); 
                else mat = constr.getJacobian().get(body);
                lastUpdate = currentUpdate;
            }
            return mat;
        }
    }
}

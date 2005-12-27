package de.kleppmann.maniation.maths;

public class ConjugateGradient {
    
    private int size;
    private Matrix a;
    private Vector b;
    private double tolerance = 1e-6;
    private int maxIter;
    private Vector diagInv;
    private Method method = Method.SIMPLE;
    
    public enum Method { SIMPLE, PRECONDITION, MAGNITUDE, MAXIMUM };

    public ConjugateGradient(Matrix a, Vector b) {
        // Determine the maximum size of the problem
        size = a.getColumns();
        if (a.getRows() > size) size = a.getRows();
        if (b.getDimension() > size) size = b.getDimension();
        // Ensure the matrix is square
        if (a.getRows() != size || a.getColumns() != size) {
            SparseMatrix anew = new SparseMatrix(size, size);
            anew.addSlice(new SparseMatrix.SliceImpl(a, 0, 0));
            this.a = anew;
        } else this.a = a;
        // Ensure the vector has the same dimension as the matrix
        if (b.getDimension() != size) {
            double[] bnew = new double[size];
            for (int i=0; i<b.getDimension(); i++) bnew[i] = b.getComponent(i);
            for (int i=b.getDimension(); i<bnew.length; i++) bnew[i] = 0.0;
            this.b = new VectorImpl(bnew);
        } else this.b = b;
        // Get inverse of the diagonal of matrix a
        double[] diag = new double[size];
        for (int i=0; i<size; i++) {
            double c = this.a.getComponent(i,i);
            if (c < 1e-16 && c > -1e-16) diag[i] = 1.0; else diag[i] = 1.0/c;
        }
        diagInv = new VectorImpl(diag);
        maxIter = 100*size;
    }

    public Vector solve() {
        int iter = 0;
        double ak, bk, bkDen = 1.0, bkNum, bNorm, xNorm, zm1Norm, zNorm, err;
        Vector x = new VectorImpl(size), r = b, rr = b, z = b.multComponents(diagInv);
        Vector zz = z, p = z, pp = z;
        if (this.method == Method.SIMPLE) bNorm = zNorm = norm(b); else bNorm = zNorm = norm(z);
        while (iter <= maxIter) {
            iter++;
            bkNum = z.mult(rr);
            if (iter > 1) {
                zz = rr.multComponents(diagInv);
                bk = bkNum / bkDen;
                p = p.mult(bk).add(z);
                pp = pp.mult(bk).add(zz);
            }
            bkDen = bkNum;
            z = a.mult(p);
            ak = bkNum/pp.mult(z);
            zz = a.mult(pp);
            x = x.add(p.mult(ak));
            r = r.subtract(z.mult(ak));
            rr = rr.subtract(zz.mult(ak));
            z = r.multComponents(diagInv);
            if (this.method == Method.SIMPLE) err = norm(r)/bNorm; else
            if (this.method == Method.PRECONDITION) err = norm(z)/bNorm;
            else {
                zm1Norm = zNorm;
                zNorm = norm(z);
                if (Math.abs(zm1Norm - zNorm) > 1e-14*zNorm)
                    err = Math.abs(ak) * norm(p) * zNorm / Math.abs(zm1Norm - zNorm);
                    else continue;
                xNorm = norm(x);
                if (err <= 0.5*xNorm) err /= xNorm; else continue;
            }
            if (err <= tolerance) break;
        }
        return x;
    }
    
    private double norm(Vector v) {
        if (this.method != Method.MAXIMUM) {
            double sum = 0.0;
            for (int i=v.getDimension()-1; i>=0; i--) {
                double val = v.getComponent(i);
                sum += val*val;
            }
            return Math.sqrt(sum);
        } else {
            double max = 0.0;
            for (int i=v.getDimension()-1; i>=0; i--) {
                double val = Math.abs(v.getComponent(i));
                if (val > max) max = val;
            }
            return max;
        }
    }
}

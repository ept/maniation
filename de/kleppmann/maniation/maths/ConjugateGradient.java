package de.kleppmann.maniation.maths;

public class ConjugateGradient {
    
    private int size;
    private Matrix[] alist;
    private Vector b;
    private double tolerance = 1e-5;
    private int maxIter;
    private Method method = Method.SIMPLE;
    
    public enum Method { SIMPLE, PRECONDITION, MAGNITUDE, MAXIMUM };

    public ConjugateGradient(Matrix[] alist, Vector b) {
        if (alist.length == 0) throw new IllegalArgumentException();
        // Determine the maximum size of the problem
        size = b.getDimension();
        for (Matrix a : alist) {
            if (a.getRows() > size) size = a.getRows();
            if (a.getColumns() > size) size = a.getColumns();
        }
        // Ensure the matrices are square
        this.alist = new Matrix[alist.length];
        for (int i=0; i<alist.length; i++) {
            Matrix a = alist[i];
            if (a.getRows() != size || a.getColumns() != size) {
                SparseMatrix.Slice[] slices = new SparseMatrix.Slice[1];
                slices[0] = new SparseMatrix.SliceImpl(a, 0, 0);
                SparseMatrix anew = new SparseMatrix(size, size, slices);
                this.alist[i] = anew;
            } else this.alist[i] = a;
        }
        // Ensure the vector has the same dimension as the matrix
        if (b.getDimension() != size) {
            double[] bnew = new double[size];
            for (int i=0; i<b.getDimension(); i++) bnew[i] = b.getComponent(i);
            for (int i=b.getDimension(); i<bnew.length; i++) bnew[i] = 0.0;
            this.b = new VectorImpl(bnew);
        } else this.b = b;
        // Other stuff
        maxIter = 100*size;
    }
    
    public Vector solve() {
        //System.err.println("*** Conjugate gradient solver started");
        int iter = 0;
        double ak, bk, bkDen = 1.0, bkNum, bNorm, xNorm, zm1Norm, zNorm, err, minerr = 1e20;
        Vector minx = null;
        Vector x = new VectorImpl(size), r = b, rr = b, z = b;
        Vector zz = z, p = z, pp = z;
        if (this.method == Method.SIMPLE) bNorm = zNorm = norm(b); else bNorm = zNorm = norm(z);
        if (Math.abs(bNorm) < 1e-15) return x;
        while (iter <= maxIter) {
            iter++;
            bkNum = z.mult(rr);
            if (iter > 1) {
                zz = rr;
                bk = bkNum / bkDen;
                p = p.mult(bk).add(z);
                pp = pp.mult(bk).add(zz);
            }
            bkDen = bkNum;
            z = p; zz = pp;
            for (int i=alist.length-1; i>=0; i--) {
                z = alist[i].mult(z);
                zz = alist[i].mult(zz);
            }
            ak = bkNum/pp.mult(z);
            x = x.add(p.mult(ak));
            r = r.subtract(z.mult(ak));
            rr = rr.subtract(zz.mult(ak));
            z = r;
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
            //System.err.println("Iteration " + iter + ": error " + err);
            if (err <= minerr) { minx = x; minerr = err; }
            if (err > 10000*minerr) {
                System.err.println("*** Divergence detected ***");
                return minx; // catch algorithm if it's diverging
            }
            if (err <= tolerance) break;
        }
        //System.err.print("(" + iter + ")");
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

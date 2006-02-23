package de.kleppmann.maniation.maths;

public class ConjugateGradient {
    
    private int size;
    private Matrix[] alist;
    private Vector b;
    private double tolerance = 1e-6;
    private int maxIter;
    //private Vector diagInv;
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
        //calcDiagonal();
        maxIter = 100*size;
    }
    
    /*private void calcDiagonal() {
        // Get the diagonal of the product of all matrices a
        double[] diag = new double[size];
        if (alist.length == 1) {
            for (int i=0; i<size; i++) diag[i] = alist[0].getComponent(i,i);
        } else if (alist.length == 2) {
            for (int i=0; i<size; i++) {
                diag[i] = 0.0;
                for (int j=0; j<size; j++)
                    diag[i] += alist[0].getComponent(i,j)*alist[1].getComponent(j,i);
            }                    
        } else {
            Matrix middle = alist[1], end = alist[alist.length-1];
            for (int i=2; i<alist.length-1; i++) middle = middle.mult(alist[i]);
            for (int i=0; i<size; i++) {
                diag[i] = 0.0;
                for (int j=0; j<size; j++)
                    for (int k=0; k<size; k++)
                        diag[i] += alist[0].getComponent(i,j)*middle.getComponent(j,k)*
                                end.getComponent(k,i);
            }
        }
        // Calculate inverse
        for (int i=0; i<size; i++)
            if ((diag[i] < 1e-16) && (diag[i] > -1e-16)) diag[i] = 1.0;
            else diag[i] = 1.0/diag[i];
        diagInv = new VectorImpl(diag);
    }*/

    public Vector solve() {
        //System.out.println("*** Conjugate gradient solver started");
        int iter = 0;
        double ak, bk, bkDen = 1.0, bkNum, bNorm, xNorm, zm1Norm, zNorm, err;
        Vector x = new VectorImpl(size), r = b, rr = b, z = b/*.multComponents(diagInv)*/;
        Vector zz = z, p = z, pp = z;
        if (this.method == Method.SIMPLE) bNorm = zNorm = norm(b); else bNorm = zNorm = norm(z);
        if (Math.abs(bNorm) < 1e-15) return x;
        while (iter <= maxIter) {
            iter++;
            bkNum = z.mult(rr);
            if (iter > 1) {
                zz = rr/*.multComponents(diagInv)*/;
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
            z = r/*.multComponents(diagInv)*/;
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
            //System.out.println("Iteration " + iter + ": error " + err);
            if (err <= tolerance) break;
        }
        //System.out.print("(" + iter + ")");
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

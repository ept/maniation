package de.kleppmann.maniation.maths;

import java.text.DecimalFormat;
import java.util.List;

public class SparseMatrix implements Matrix {
    
    private int rows, columns;
    private Slice[] slices;
    private SparseMatrix transpose, inverse;
    
    private SparseMatrix(boolean nonsense, int rows, int columns, Slice[] slices) {
        // Create transpose
        this.rows = columns;
        this.columns = rows;
        this.slices = new Slice[slices.length];
        for (int i=0; i<slices.length; i++) {
            this.slices[i] = new SliceTranspose(slices[i]);
        }
    }
    
    private SparseMatrix(int nonsense, int rows, int columns, Slice[] slices) {
        // Create inverse
        this.rows = rows;
        this.columns = columns;
        this.slices = new Slice[slices.length];
        for (int i=0; i<slices.length; i++) {
            if (slices[i].getStartColumn() != slices[i].getStartRow())
                throw new UnsupportedOperationException();
            this.slices[i] = new SliceInverse(slices[i]);
        }
    }

    public SparseMatrix(int rows, int columns, Slice[] slices) {
        this.rows = rows;
        this.columns = columns;
        List<Slice> sList = new java.util.ArrayList<Slice>();
        for (Slice s : slices) {
            Matrix m = s.getMatrix();
            if (m instanceof SparseMatrix) {
                for (Slice s2 : ((SparseMatrix) m).slices) sList.add(s2);
            } else sList.add(s);
        }
        this.slices = sList.toArray(new Slice[sList.size()]);
        this.transpose = new SparseMatrix(true, rows, columns, this.slices);
        this.transpose.transpose = this;
        try {
            this.inverse = new SparseMatrix(1, rows, columns, this.slices);
            this.inverse.inverse = this;
        } catch (UnsupportedOperationException e) {
            this.inverse = null;
        }
    }
    
    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public double getComponent(int row, int column) {
        for (Slice s : slices) {
            if ((row >= s.getStartRow()) && (row - s.getStartRow() < s.getMatrix().getRows()) &&
                    (column >= s.getStartColumn()) &&
                    (column - s.getStartColumn() < s.getMatrix().getColumns()))
                return s.getMatrix().getComponent(row - s.getStartRow(), column - s.getStartColumn());
        }
        return 0.0;
    }

    public SparseMatrix transpose() {
        return this.transpose;
    }

    public SparseMatrix inverse() {
        if (this.inverse != null) return this.inverse;
        throw new UnsupportedOperationException();
    }

    public SparseMatrix mult(double scalar) {
        Slice[] scaled = new Slice[slices.length];
        for (int i=0; i<slices.length; i++) scaled[i] = new SliceScaled(slices[i], scalar); 
        return new SparseMatrix(rows, columns, scaled);
    }

    public Matrix mult(Matrix other) {
        if (this.getColumns() != other.getRows()) throw new IllegalArgumentException();
        double[][] t = new double[getRows()][other.getColumns()];
        for (int i=0; i<rows; i++)
            for (int j=0; j<other.getColumns(); j++) {
                t[i][j] = 0.0;
                for (int k=0; k<columns; k++)
                    t[i][j] += getComponent(i,k)*other.getComponent(k,j);
            }
        return new MatrixImpl(t);
    }

    public Vector mult(Vector vec) {
        if (this.getColumns() != vec.getDimension()) throw new IllegalArgumentException();
        double[] result = new double[this.getRows()];
        for (int i=0; i<result.length; i++) result[i] = 0.0;
        for (Slice slice : slices) {
            double[] subvec = new double[slice.getMatrix().getColumns()];
            int offs = slice.getStartColumn();
            for (int i=0; i<subvec.length; i++) subvec[i] = vec.getComponent(offs+i);
            Vector prod = slice.getMatrix().mult(new VectorImpl(subvec));
            offs = slice.getStartRow();
            for (int i=prod.getDimension()-1; i>=0; i--)
                result[i+offs] += prod.getComponent(i);
        }
        return new VectorImpl(result);
    }

    public Matrix add(Matrix other) {
        if ((getRows() != other.getRows()) || (getColumns() != other.getColumns()))
            throw new IllegalArgumentException();
        double[][] t = new double[getRows()][getColumns()];
        for (int i=0; i<rows; i++)
            for (int j=0; j<columns; j++)
                t[i][j] = getComponent(i,j) + other.getComponent(i,j);
        return new MatrixImpl(t);
    }

    public Matrix subtract(Matrix other) {
        if ((getRows() != other.getRows()) || (getColumns() != other.getColumns()))
            throw new IllegalArgumentException();
        double[][] t = new double[getRows()][getColumns()];
        for (int i=0; i<rows; i++)
            for (int j=0; j<columns; j++)
                t[i][j] = getComponent(i,j) - other.getComponent(i,j);
        return new MatrixImpl(t);
    }
    
    public String toString() {
        DecimalFormat format = new DecimalFormat("#####0.0000000000");
        String result = "[";
        for (int i=0; i<getRows(); i++)
            for (int j=0; j<getColumns(); j++) {
                result += format.format(getComponent(i,j));
                if (j < getColumns() - 1) result += ", "; else
                if (i < getRows() - 1) result += "; "; else result += "]";
            }
        return result;
    }

        
    public interface Slice {
        int getStartRow();
        int getStartColumn();
        Matrix getMatrix();
    }
    

    public static class SliceImpl implements Slice {
        private Matrix m;
        private int startRow, startColumn;
        
        public SliceImpl(Matrix m, int startRow, int startColumn) {
            this.m = m; this.startRow = startRow; this.startColumn = startColumn;
        }
        public int getStartRow() {
            return startRow;
        }
        public int getStartColumn() {
            return startColumn;
        }
        public Matrix getMatrix() {
            return m;
        }
    }
    
    
    private static class SliceTranspose implements Slice {
        private Slice origin;
        public SliceTranspose(Slice origin) {
            this.origin = origin;
        }
        public Matrix getMatrix() {
            return origin.getMatrix().transpose();
        }
        public int getStartColumn() {
            return origin.getStartRow();
        }
        public int getStartRow() {
            return origin.getStartColumn();
        }
    }

    
    private static class SliceInverse implements Slice {
        private Slice origin;
        public SliceInverse(Slice origin) {
            if (origin.getStartRow() != origin.getStartColumn())
                throw new UnsupportedOperationException();
            this.origin = origin;
        }
        public Matrix getMatrix() {
            return origin.getMatrix().inverse();
        }
        public int getStartColumn() {
            return origin.getStartColumn();
        }
        public int getStartRow() {
            return origin.getStartRow();
        }
    }
    
    
    private static class SliceScaled implements Slice {
        private Matrix scaled;
        private int row, col;
        public SliceScaled(Slice origin, double factor) {
            this.scaled = origin.getMatrix().mult(factor);
            this.row = origin.getStartRow();
            this.col = origin.getStartColumn();
        }
        public Matrix getMatrix() {
            return scaled;
        }
        public int getStartColumn() {
            return col;
        }
        public int getStartRow() {
            return row;
        }
    }
}

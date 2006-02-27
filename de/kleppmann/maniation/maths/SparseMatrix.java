package de.kleppmann.maniation.maths;

import java.util.List;

public class SparseMatrix implements Matrix {
    
    private int rows, columns;
    private Slice[] slices;
    private SliceTree root;
    private SparseMatrix transpose, inverse;
    
    private SparseMatrix(boolean nonsense, int rows, int columns, Slice[] slices) {
        // Create transpose
        this.rows = columns;
        this.columns = rows;
        this.root = new SliceTree();
        this.slices = new Slice[slices.length];
        for (int i=0; i<slices.length; i++) {
            this.slices[i] = new SliceTranspose(slices[i]);
            root.addSlice(this.slices[i]);
        }
    }
    
    private SparseMatrix(int nonsense, int rows, int columns, Slice[] slices) {
        // Create inverse
        this.rows = rows;
        this.columns = columns;
        this.root = new SliceTree();
        this.slices = new Slice[slices.length];
        for (int i=0; i<slices.length; i++) {
            this.slices[i] = new SliceInverse(slices[i]);
            root.addSlice(this.slices[i]);
        }
    }

    public SparseMatrix(int rows, int columns, Slice[] slices) {
        this.rows = rows;
        this.columns = columns;
        this.root = new SliceTree();
        List<Slice> sList = new java.util.ArrayList<Slice>();
        for (Slice s : slices) {
            Matrix m = s.getMatrix();
            if (m instanceof SparseMatrix) {
                for (Slice s2 : ((SparseMatrix) m).slices) {
                    sList.add(s2);
                    root.addSlice(s2);
                }
            } else {
                sList.add(s);
                root.addSlice(s);
            }
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
        return this.root.getComponent(row, column);
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

    
    private class SliceTree {
        private SliceTree left = null, right = null;
        private Slice slice = null;
        private int minRow, minCol, maxRow, maxCol;
        private boolean splitRows;
        
        public SliceTree() {
            minRow = minCol = 0;
            maxRow = rows - 1;
            maxCol = columns - 1;
        }
        
        private SliceTree(SliceTree parent, boolean splitRows, boolean upper) {
            if (splitRows) {
                minCol = parent.minCol; maxCol = parent.maxCol;
                if (upper) {
                    minRow = (parent.minRow + parent.maxRow) / 2 + 1;
                    maxRow = parent.maxRow;
                } else {
                    minRow = parent.minRow;
                    maxRow = (parent.minRow + parent.maxRow) / 2;
                }
            } else {
                minRow = parent.minRow; maxRow = parent.maxRow;
                if (upper) {
                    minCol = (parent.minCol + parent.maxCol) / 2 + 1;
                    maxCol = parent.maxCol;
                } else {
                    minCol = parent.minCol;
                    maxCol = (parent.minCol + parent.maxCol) / 2;
                }
            }
            if ((minCol > maxCol) || (minRow > maxRow))
                throw new IllegalArgumentException("overlapping slices");
        }
        
        public void addSlice(Slice s) {
            if ((s.getStartRow() > maxRow) || (s.getStartColumn() > maxCol) ||
                    (s.getStartRow() + s.getMatrix().getRows() <= minRow) ||
                    (s.getStartColumn() + s.getMatrix().getColumns() <= minCol)) return;
            if ((left == null) && (right == null)) {
                if (slice == null) {
                    slice = s;
                    return;
                }
                splitRows = (maxRow - minRow > maxCol - minCol);
                left = new SliceTree(this, splitRows, false);
                right = new SliceTree(this, splitRows, true);
            }
            left.addSlice(s);
            right.addSlice(s);
        }
        
        public double getComponent(int row, int column) {
            if ((left == null) && (right == null)) {
                if (slice == null) return 0.0;
                row -= slice.getStartRow();
                column -= slice.getStartColumn();
                if ((row < 0) || (column < 0) || (row >= slice.getMatrix().getRows()) ||
                        (column >= slice.getMatrix().getColumns())) return 0.0;
                return slice.getMatrix().getComponent(row, column);
            } else {
                if (splitRows) {
                    if (row <= (minRow + maxRow) / 2)
                        return left.getComponent(row, column); else
                        return right.getComponent(row, column);
                } else {
                    if (column <= (minCol + maxCol) / 2)
                        return left.getComponent(row, column); else
                        return right.getComponent(row, column);
                }
            }
        }
    }
}

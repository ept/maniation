package de.kleppmann.maniation.maths;

import java.util.List;

public class SparseMatrix implements Matrix {
    
    public interface Slice {
        int getStartRow();
        int getStartColumn();
        int getRows();
        int getColumns();
        Matrix getMatrix();
    }
    

    public static class SliceImpl implements Slice {
        private Matrix m;
        private int startRow, startColumn, rows, columns;
        
        public SliceImpl(Matrix m, int startRow, int startColumn) {
            this.m = m; this.startRow = startRow; this.startColumn = startColumn;
            this.rows = m.getRows(); this.columns = m.getColumns();
        }
        public int getStartRow() {
            return startRow;
        }
        public int getStartColumn() {
            return startColumn;
        }
        public int getRows() {
            return rows;
        }
        public int getColumns() {
            return columns;
        }
        public Matrix getMatrix() {
            return m;
        }
    }

    private int rows, columns;
    private List<Slice> slices = new java.util.ArrayList<Slice>();

    public SparseMatrix(int rows, int columns) {
        this.rows = rows; this.columns = columns;
    }
    
    public void addSlice(Slice slice) {
        slices.add(slice);
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public double getComponent(int row, int column) {
        // TODO Auto-generated method stub
        return 0;
    }

    public Matrix transpose() {
        // TODO Auto-generated method stub
        return null;
    }

    public Matrix inverse() {
        // TODO Auto-generated method stub
        return null;
    }

    public Matrix mult(double scalar) {
        // TODO Auto-generated method stub
        return null;
    }

    public Matrix mult(Matrix other) {
        // TODO Auto-generated method stub
        return null;
    }

    public Vector mult(Vector vec) {
        // TODO Auto-generated method stub
        return null;
    }

    public Matrix add(Matrix other) {
        // TODO Auto-generated method stub
        return null;
    }

    public Matrix subtract(Matrix other) {
        // TODO Auto-generated method stub
        return null;
    }

}

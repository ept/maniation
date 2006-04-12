package de.kleppmann.maniation.maths;

import java.text.DecimalFormat;

public class MatrixImpl implements Matrix {
    
    private double[][] m;
    private MatrixImpl transpose = null;

    public MatrixImpl(int rows, int columns) {
        if ((rows < 1) || (columns < 1)) throw new IllegalArgumentException();
        m = new double[rows][columns];
        for (int i=0; i<rows; i++)
            for (int j=0; j<columns; j++)
                if (i==j) m[i][j] = 1.0; else m[i][j] = 0.0;
    }
    
    public MatrixImpl(double[][] components) {
        m = components;
    }

    public int getRows() {
        return m.length;
    }

    public int getColumns() {
        return m[0].length;
    }

    public double getComponent(int row, int column) {
        return m[row][column];
    }

    public Matrix transpose() {
        if (transpose != null) return transpose;
        double[][] t = new double[getColumns()][getRows()];
        for (int i=0; i<getRows(); i++)
            for (int j=0; j<getColumns(); j++)
                t[j][i] = getComponent(i,j);
        transpose = new MatrixImpl(t);
        return transpose;
    }

    public Matrix inverse() {
        throw new UnsupportedOperationException();
    }

    public Matrix mult(double scalar) {
        double[][] t = new double[getRows()][getColumns()];
        for (int i=0; i<getRows(); i++)
            for (int j=0; j<getColumns(); j++)
                t[i][j] = scalar*getComponent(i,j);
        return new MatrixImpl(t);
    }

    public Matrix mult(Matrix other) {
        if (this.getColumns() != other.getRows()) throw new IllegalArgumentException();
        double[][] t = new double[getRows()][other.getColumns()];
        for (int i=0; i<getRows(); i++)
            for (int j=0; j<other.getColumns(); j++) {
                t[i][j] = 0.0;
                for (int k=0; k<getColumns(); k++)
                    t[i][j] += getComponent(i,k)*other.getComponent(k,j);
            }
        if ((t.length == 3) && (t[0].length == 3))
            return new Matrix33(
                t[0][0], t[0][1], t[0][2],
                t[1][0], t[1][1], t[1][2],
                t[2][0], t[2][1], t[2][2]);
        return new MatrixImpl(t);
    }

    public Vector mult(Vector vec) {
        if (this.getColumns() != vec.getDimension()) throw new IllegalArgumentException();
        double[] v = new double[vec.getDimension()];
        vec.toDoubleArray(v, 0);
        double[] result = new double[this.getRows()];
        for (int i=getRows()-1; i>=0; i--) {
            result[i] = 0.0;
            for (int j=getColumns()-1; j>=0; j--)
                result[i] += this.getComponent(i,j) * v[j];
        }
        if (result.length == 3) return new Vector3D(result[0], result[1], result[2]);
        return new VectorImpl(result);
    }

    public Matrix add(Matrix other) {
        if ((getRows() != other.getRows()) || (getColumns() != other.getColumns()))
            throw new IllegalArgumentException();
        double[][] t = new double[getRows()][getColumns()];
        for (int i=0; i<getRows(); i++)
            for (int j=0; j<getColumns(); j++)
                t[i][j] = this.getComponent(i,j) + other.getComponent(i,j);
        return new MatrixImpl(t);
    }

    public Matrix subtract(Matrix other) {
        if ((getRows() != other.getRows()) || (getColumns() != other.getColumns()))
            throw new IllegalArgumentException();
        double[][] t = new double[getRows()][getColumns()];
        for (int i=0; i<getRows(); i++)
            for (int j=0; j<getColumns(); j++)
                t[i][j] = this.getComponent(i,j) - other.getComponent(i,j);
        return new MatrixImpl(t);
    }
    
    @Override
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
}

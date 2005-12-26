package de.kleppmann.maniation.maths;

public class Matrix33 implements Matrix {
    
    private double m11, m12, m13, m21, m22, m23, m31, m32, m33;
    private Matrix33 inverse;
    
    private Matrix33(boolean nonsense) {
        m11 = m22 = m33 = 1.0;
        m12 = m13 = m21 = m23 = m31 = m32 = 0.0;
    }

    public Matrix33() {
        m11 = m22 = m33 = 1.0;
        m12 = m13 = m21 = m23 = m31 = m32 = 0.0;
        inverse = new Matrix33(true);
        inverse.inverse = this;
    }
    
    public Matrix33(double m11, double m12, double m13, double m21, 
            double m22, double m23, double m31, double m32, double m33) {
        this.m11 = m11; this.m12 = m12; this.m13 = m13;
        this.m21 = m21; this.m22 = m22; this.m23 = m23;
        this.m31 = m31; this.m32 = m32; this.m33 = m33;
        if ((m12 == 0.0) && (m13 == 0.0) && (m21 == 0.0) && (m23 == 0.0) &&
                (m31 == 0.0) && (m32 == 0.0)) {
            inverse = new Matrix33(true);
            inverse.m11 = 1.0/m11;
            inverse.m22 = 1.0/m22;
            inverse.m33 = 1.0/m33;
            inverse.inverse = this;
        } else {
            inverse = null;
        }
    }
    
    public Matrix33(Vector3D vec) {
        m11 = vec.getComponent(0);
        m22 = vec.getComponent(1);
        m33 = vec.getComponent(2);
        m12 = m13 = m21 = m23 = m31 = m32 = 0.0;
        inverse = new Matrix33(true);
        inverse.m11 = 1.0/m11;
        inverse.m22 = 1.0/m22;
        inverse.m33 = 1.0/m33;
        inverse.inverse = this;
    }

    public int getRows() {
        return 3;
    }

    public int getColumns() {
        return 3;
    }

    public double getComponent(int row, int column) {
        switch (row) {
        case 0:
            switch(column) {
            case 0: return m11;
            case 1: return m12;
            case 2: return m13;
            }
        case 1:
            switch(column) {
            case 0: return m21;
            case 1: return m22;
            case 2: return m23;
            }
        case 2:
            switch(column) {
            case 0: return m31;
            case 1: return m32;
            case 2: return m33;
            }
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public Matrix33 transpose() {
        return new Matrix33(m11, m21, m31, m12, m22, m32, m13, m23, m33);
    }

    public Matrix33 inverse() {
        if (inverse != null) return inverse;
        throw new UnsupportedOperationException();
    }

    public Matrix33 mult(double scalar) {
        return new Matrix33(scalar*m11, scalar*m12, scalar*m13,
                scalar*m21, scalar*m22, scalar*m23, scalar*m31, scalar*m32, scalar*m33);
    }
    
    public Matrix33 mult33(Matrix33 m) {
        return new Matrix33(
                m11*m.m11 + m12*m.m21 + m13*m.m31,
                m11*m.m12 + m12*m.m22 + m13*m.m32,
                m11*m.m13 + m12*m.m23 + m13*m.m33,
                m21*m.m11 + m22*m.m21 + m23*m.m31,
                m21*m.m12 + m22*m.m22 + m23*m.m32,
                m21*m.m13 + m22*m.m23 + m23*m.m33,
                m31*m.m11 + m32*m.m21 + m33*m.m31,
                m31*m.m12 + m32*m.m22 + m33*m.m32,
                m31*m.m13 + m32*m.m23 + m33*m.m33);
    }

    public Matrix mult(Matrix other) {
        if (other instanceof Matrix33) return mult33((Matrix33) other);
        if (other.getRows() != 3) throw new IllegalArgumentException();
        double[][] prod = new double[3][other.getColumns()];
        for (int i=0; i<other.getColumns(); i++) {
            prod[0][i] = m11*other.getComponent(0,i) +
                m12*other.getComponent(1,i) + m13*other.getComponent(2,i);
            prod[1][i] = m21*other.getComponent(0,i) +
                m22*other.getComponent(1,i) + m23*other.getComponent(2,i);
            prod[2][i] = m31*other.getComponent(0,i) +
                m32*other.getComponent(1,i) + m33*other.getComponent(2,i);
        }
        return new MatrixImpl(prod);
    }

    public Vector3D mult(Vector vec) {
        if (vec.getDimension() != 3) throw new IllegalArgumentException();
        double x = vec.getComponent(0);
        double y = vec.getComponent(1);
        double z = vec.getComponent(2);
        return new Vector3D(
                m11*x + m12*y + m13*z,
                m21*x + m22*y + m23*z,
                m31*x + m32*y + m33*z);
    }

    public Matrix33 add(Matrix other) {
        if (other instanceof Matrix33) {
            Matrix33 m = (Matrix33) other;
            return new Matrix33(m11 + m.m11, m12 + m.m12, m13 + m.m13,
                    m21 + m.m21, m22 + m.m22, m23 + m.m23,
                    m31 + m.m31, m32 + m.m32, m33 + m.m33);
        }
        if ((other.getRows() != 3) || (other.getColumns() != 3))
            throw new IllegalArgumentException();
        return new Matrix33(m11 + other.getComponent(0,0),
                m12 + other.getComponent(0,1),
                m13 + other.getComponent(0,2),
                m21 + other.getComponent(1,0),
                m22 + other.getComponent(1,1),
                m23 + other.getComponent(1,2),
                m31 + other.getComponent(2,0),
                m32 + other.getComponent(2,1),
                m33 + other.getComponent(2,2));
    }

    public Matrix33 subtract(Matrix other) {
        if (other instanceof Matrix33) {
            Matrix33 m = (Matrix33) other;
            return new Matrix33(m11 - m.m11, m12 - m.m12, m13 - m.m13,
                    m21 - m.m21, m22 - m.m22, m23 - m.m23,
                    m31 - m.m31, m32 - m.m32, m33 - m.m33);
        }
        if ((other.getRows() != 3) || (other.getColumns() != 3))
            throw new IllegalArgumentException();
        return new Matrix33(m11 - other.getComponent(0,0),
                m12 - other.getComponent(0,1),
                m13 - other.getComponent(0,2),
                m21 - other.getComponent(1,0),
                m22 - other.getComponent(1,1),
                m23 - other.getComponent(1,2),
                m31 - other.getComponent(2,0),
                m32 - other.getComponent(2,1),
                m33 - other.getComponent(2,2));
    }
}

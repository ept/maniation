package de.kleppmann.maniation.maths;

import java.text.DecimalFormat;

public class Vector3D implements Vector {
    
    private double x, y, z;
    
    public Vector3D() {
        this.x = 0.0; this.y = 0.0; this.z = 0.0;
    }

    public Vector3D(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
    }
    
    public Vector3D cross(Vector3D right) {
        return new Vector3D(
                this.y*right.z - this.z*right.y,
                this.z*right.x - this.x*right.z,
                this.x*right.y - this.y*right.x);
    }

    public int getDimension() {
        return 3;
    }

    public double getComponent(int index) {
        switch (index) {
        case 0: return x;
        case 1: return y;
        case 2: return z;
        default: throw new ArrayIndexOutOfBoundsException();
        }
    }

    public Vector3D mult(double scalar) {
        return new Vector3D(scalar*x, scalar*y, scalar*z);
    }

    public double mult(Vector v) {
        if (v instanceof Vector3D) {
            Vector3D vv = (Vector3D) v;
            return x*vv.x + y*vv.y + z*vv.z;
        }
        if (v.getDimension() != 3) throw new IllegalArgumentException();
        return x*v.getComponent(0) + y*v.getComponent(1) + z*v.getComponent(2);
    }
    
    public Vector3D multComponents(Vector v) {
        if (v instanceof Vector3D) {
            Vector3D vv = (Vector3D) v;
            return new Vector3D(x*vv.x, y*vv.y, z*vv.z);
        }
        if (v.getDimension() != 3) throw new IllegalArgumentException();
        return new Vector3D(x*v.getComponent(0), y*v.getComponent(1),
                z*v.getComponent(2));
    }

    public Vector3D add(Vector v) {
        if (v instanceof Vector3D) {
            Vector3D vv = (Vector3D) v;
            return new Vector3D(x+vv.x, y+vv.y, z+vv.z);
        }        
        if (v.getDimension() != 3) throw new IllegalArgumentException();
        return new Vector3D(x+v.getComponent(0), y+v.getComponent(1), z+v.getComponent(2));
    }

    public Vector3D subtract(Vector v) {
        if (v instanceof Vector3D) {
            Vector3D vv = (Vector3D) v;
            return new Vector3D(x-vv.x, y-vv.y, z-vv.z);
        }        
        if (v.getDimension() != 3) throw new IllegalArgumentException();
        return new Vector3D(x-v.getComponent(0), y-v.getComponent(1), z-v.getComponent(2));
    }

    public void toDoubleArray(double[] array, int offset) {
        array[offset] = x;
        array[offset+1] = y;
        array[offset+2] = z;
    }
    
    public String toString() {
        DecimalFormat format = new DecimalFormat("######0.00000");
        return "Vector3D(" + format.format(x) + ", " + format.format(y) +
            ", " + format.format(z) + ")";

    }
}

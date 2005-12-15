package de.kleppmann.maniation.maths;

import java.text.DecimalFormat;


public class Quaternion {
    
    private double w, x, y, z;
    private Quaternion inverse;
    
    private Quaternion() {}

    public Quaternion(double w, double x, double y, double z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
        double m = w*w + x*x + y*y + z*z;
        this.inverse = new Quaternion();
        this.inverse.w = w/m;
        this.inverse.x = -x/m;
        this.inverse.y = -y/m;
        this.inverse.z = -z/m;
        this.inverse.inverse = this;
    }

    public double getW() {
        return w;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String toString() {
        DecimalFormat format = new DecimalFormat("######0.00000");
        return "Quaternion(w: " + format.format(w) +
            ", x: " + format.format(x) + 
            ", y: " + format.format(y) +
            ", z: " + format.format(z) + ")";
    }

    public Quaternion mult(Quaternion other) {
        return new Quaternion(
                this.w*other.w - this.x*other.x - this.y*other.y - this.z*other.z,
                this.w*other.x + this.x*other.w + this.y*other.z - this.z*other.y,
                this.w*other.y + this.y*other.w + this.z*other.x - this.x*other.z,
                this.w*other.z + this.z*other.w + this.x*other.y - this.y*other.x
        );
    }
    
    public Quaternion getInverse() {
        return inverse;
    }
    
    public Vector transform(Vector v) {
        if (v.getDimension() != 3) throw new IllegalArgumentException();
        Quaternion vq = new Quaternion(0.0, v.getElement(0), v.getElement(1), v.getElement(2));
        Quaternion t = mult(vq).mult(inverse);
        return new Vector(t.x, t.y, t.z);
    }

    public Quaternion interpolateTo(Quaternion dest, double amount) {
        double theta = Math.acos(this.x*dest.x + this.y*dest.y + this.z*dest.z + this.w*dest.w);
        double sinTheta = Math.sin(theta);
        double v1 = Math.sin((1.0 - amount)*theta) / sinTheta;
        double v2 = Math.sin(amount*theta) / sinTheta;
        return new Quaternion(
                v1*this.w + v2*dest.w,
                v1*this.x + v2*dest.x,
                v1*this.y + v2*dest.y,
                v1*this.z + v2*dest.z);
    }
    
    public static Quaternion getXRotation(double angle) {
        return new Quaternion(Math.cos(angle/2.0), Math.sin(angle/2.0), 0.0, 0.0);
    }
    
    public static Quaternion getYRotation(double angle) {
        return new Quaternion(Math.cos(angle/2.0), 0.0, Math.sin(angle/2.0), 0.0);
    }
    
    public static Quaternion getZRotation(double angle) {
        return new Quaternion(Math.cos(angle/2.0), 0.0, 0.0, Math.sin(angle/2.0));
    }
}

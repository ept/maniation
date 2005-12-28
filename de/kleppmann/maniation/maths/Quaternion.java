package de.kleppmann.maniation.maths;

import java.text.DecimalFormat;


public class Quaternion {
    
    private double w, x, y, z, mag;
    private Quaternion inverse;
    
    private Quaternion(boolean nonsense) {}
    
    public Quaternion() {
        this.w = 1.0; this.x = 0.0; this.y = 0.0; this.z = 0.0;
        this.inverse = new Quaternion(true);
        inverse.w = 1.0; inverse.x = 0.0; inverse.y = 0.0; inverse.z = 0.0;
        inverse.inverse = this;
    }

    public Quaternion(double w, double x, double y, double z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
        double m = w*w + x*x + y*y + z*z;
        this.inverse = new Quaternion(true);
        this.inverse.w = w/m;
        this.inverse.x = -x/m;
        this.inverse.y = -y/m;
        this.inverse.z = -z/m;
        this.inverse.inverse = this;
        this.mag = Math.sqrt(m);
    }
    
    public Quaternion(Vector3D v) {
        this.w = 0.0;
        this.x = v.getComponent(0);
        this.y = v.getComponent(1);
        this.z = v.getComponent(2);
        this.inverse = null;
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
    
    public Quaternion add(Quaternion other) {
        return new Quaternion(this.w+other.w, this.x+other.x,
                this.y+other.y, this.z+other.z);
    }
    
    public Quaternion subtract(Quaternion other) {
        return new Quaternion(this.w-other.w, this.x-other.x,
                this.y-other.y, this.z-other.z);
    }
    
    public Quaternion quergs(Quaternion delta) {
        double mag = delta.getMagnitude();
        if (mag < 1e-20) return this;
        long n = Math.round(mag/Math.PI - 0.5);
        double d = mag - Math.PI*(n + 0.5);
        if ((d < 1e-6) && (d > -1e-6)) return new Quaternion(delta.w/mag,
                delta.x/mag, delta.y/mag, delta.z/mag);
        double t = Math.tan(mag)/mag;
        double wn = this.w + t*delta.w;
        double xn = this.x + t*delta.x;
        double yn = this.y + t*delta.y;
        double zn = this.z + t*delta.z;
        mag = Math.sqrt(wn*wn + xn*xn + yn*yn + zn*zn);
        return new Quaternion(wn/mag, xn/mag, yn/mag, zn/mag);
    }
    
    public Quaternion getInverse() {
        return inverse;
    }
    
    public Vector3D transform(Vector3D v) {
        if (v.getDimension() != 3) throw new IllegalArgumentException();
        Quaternion t = mult(new Quaternion(v)).mult(inverse);
        return new Vector3D(t.x, t.y, t.z);
    }
    
    public double getMagnitude() {
        return mag;
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
    
    public EulerAngles toEuler() {
        double sy = 2.0*(w*y - x*z);
        double cy = Math.sqrt(1 - sy*sy);
        double sx, cx, sz, cz;
        
        if (Math.abs(cy) < 1e-6) {
            sx = 2.0*(w*x - y*z);
            cx = 1.0 - 2.0*(x*x + z*z);
            sz = 0.0;
            cz = 1.0;
        } else {
            sx = 2.0*(y*z + w*x)/cy;
            cx = (1.0 - 2.0*(x*x + y*y))/cy;
            sz = 2.0*(x*y + w*z)/cy;
            cz = (1.0 - 2.0*(y*y + z*z))/cy;
        }

        double rotX = Math.acos(cx); if (sx < 0) rotX = 2*Math.PI - rotX;
        double rotY = Math.acos(cy); if (sy < 0) rotY = 2*Math.PI - rotY;
        double rotZ = Math.acos(cz); if (sz < 0) rotZ = 2*Math.PI - rotZ;
        return new EulerAngles(EulerAngles.Convention.ROLL_PITCH_YAW, rotX, rotY, rotZ);
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

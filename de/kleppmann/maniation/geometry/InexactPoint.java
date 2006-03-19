package de.kleppmann.maniation.geometry;

import de.kleppmann.maniation.maths.Vector3D;

public class InexactPoint {
    public static final double TOLERANCE = 1e-5;
    private Vector3D v;
    private long x, y, z;
    
    InexactPoint(Vector3D v) {
        this.v = v;
        this.x = Math.round(v.getComponent(0)/TOLERANCE);
        this.y = Math.round(v.getComponent(1)/TOLERANCE);
        this.z = Math.round(v.getComponent(2)/TOLERANCE);
    }
    
    public Vector3D getPosition() {
        return v;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof InexactPoint) {
            InexactPoint other = (InexactPoint) obj;
            return (this.x == other.x) && (this.y == other.y) && (this.z == other.z);
        } else return false;
    }

    @Override
    public int hashCode() {
        long result = (x % (1l << 32)) ^ ((x / (1l << 32)) >> 32);
        result ^= (y % (1l << 32)) ^ ((y / (1l << 32)) >> 32);
        result ^= (z % (1l << 32)) ^ ((z / (1l << 32)) >> 32);
        return (int) result;
    }
}

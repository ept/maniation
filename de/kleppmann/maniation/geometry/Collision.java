package de.kleppmann.maniation.geometry;

import java.util.List;


public class Collision {

    List<CollisionPoint> intersections = new java.util.ArrayList<CollisionPoint>();
    boolean colliding = false;
    
    public void addIntersection() {
        colliding = true;
    }
    
    public void addIntersection(double x, double y, double z,
                double normx, double normy, double normz) {
        intersections.add(new CollisionPoint(x, y, z, normx, normy, normz));
    }
    
    public boolean isColliding() {
        return colliding;
    }
    
    private class CollisionPoint {
        double x, y, z, normx, normy, normz;
        
        CollisionPoint(double x, double y, double z,
                double normx, double normy, double normz) {
            this.x = x; this.y = y; this.z = z;
            double mag = Math.sqrt(normx*normx + normy*normy + normz*normz);
            this.normx = normx / mag;
            this.normy = normy / mag;
            this.normz = normz / mag;
        }
    }

}

package de.kleppmann.maniation.geometry;

import java.util.List;
import de.kleppmann.maniation.maths.Vector3D;

public class Collision {

    List<CollisionPoint> intersections = new java.util.ArrayList<CollisionPoint>();
    
    public void addIntersection(Vector3D lineFrom, Vector3D lineTo, MeshTriangle tri1, MeshTriangle tri2) {
        intersections.add(new CollisionPoint(lineFrom, lineTo, tri1, tri2));
    }
    
    public boolean isColliding() {
        return intersections.size() > 0;
    }
    
    
    private class CollisionPoint {
        Vector3D lineFrom, lineTo;
        MeshTriangle tri1, tri2;
        
        CollisionPoint(Vector3D lineFrom, Vector3D lineTo, MeshTriangle tri1, MeshTriangle tri2) {
            this.lineFrom = lineFrom; this.lineTo = lineTo;
            this.tri1 = tri1; this.tri2 = tri2;
        }
    }
}

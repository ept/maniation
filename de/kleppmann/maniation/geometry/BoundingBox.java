package de.kleppmann.maniation.geometry;

import java.text.DecimalFormat;

import de.kleppmann.maniation.maths.Vector3D;


public class BoundingBox {
    
    boolean empty;
    double maxx, minx, maxy, miny, maxz, minz;
    
    public BoundingBox() {
        empty = true;
    }

    public BoundingBox(double maxx, double minx, double maxy, double miny,
            double maxz, double minz) {
        empty = false;
        this.maxx = maxx; this.minx = minx;
        this.maxy = maxy; this.miny = miny;
        this.maxz = maxz; this.minz = minz;
    }
    
    public BoundingBox(BoundingBox bbox1, BoundingBox bbox2) {
        empty = false;
        if (bbox1.empty && bbox2.empty) empty = true; else
        if (bbox1.empty) apply(bbox2); else
        if (bbox2.empty) apply(bbox1); else superBox(bbox1, bbox2);
    }
    
    public BoundingBox(MeshTriangle[] triangles) {
        empty = false;
        if (triangles.length > 0) apply(triangles[0].bbox); else empty = true;
        for (int i=1; i<triangles.length; i++) superBox(this, triangles[i].bbox);
    }
    
    private void apply(BoundingBox box) {
        this.maxx = box.maxx; this.minx = box.minx;
        this.maxy = box.maxy; this.miny = box.miny;
        this.maxz = box.maxz; this.minz = box.minz;
    }
    
    private void superBox(BoundingBox bbox1, BoundingBox bbox2) {
        this.maxx = bbox1.maxx > bbox2.maxx ? bbox1.maxx : bbox2.maxx;
        this.minx = bbox1.minx < bbox2.minx ? bbox1.minx : bbox2.minx;
        this.maxy = bbox1.maxy > bbox2.maxy ? bbox1.maxy : bbox2.maxy;
        this.miny = bbox1.miny < bbox2.miny ? bbox1.miny : bbox2.miny;
        this.maxz = bbox1.maxz > bbox2.maxz ? bbox1.maxz : bbox2.maxz;
        this.minz = bbox1.minz < bbox2.minz ? bbox1.minz : bbox2.minz;
    }

    public boolean intersects(BoundingBox other) {
        if (this.empty || other.empty) return false;
        return
           (((this.minx >= other.minx) && (this.minx <= other.maxx)) ||
            ((this.maxx >= other.minx) && (this.maxx <= other.maxx)) ||
            ((this.maxx >= other.minx) && (this.minx <= other.minx))) &&
           (((this.miny >= other.miny) && (this.miny <= other.maxy)) ||
            ((this.maxy >= other.miny) && (this.maxy <= other.maxy)) ||
            ((this.maxy >= other.miny) && (this.miny <= other.miny))) &&
           (((this.minz >= other.minz) && (this.minz <= other.maxz)) ||
            ((this.maxz >= other.minz) && (this.maxz <= other.maxz)) ||
            ((this.maxz >= other.minz) && (this.minz <= other.minz)));
    }
    
    public boolean intersects(Vector3D p1, Vector3D p2) {
        Vector3D pl1 = new Vector3D(maxx, miny, minz);
        Vector3D pl2 = new Vector3D(minx, maxy, minz);
        Vector3D pl3 = new Vector3D(minx, miny, maxz);
        Vector3D pl4 = new Vector3D(maxx, maxy, maxz);
        Vector3D dir = p2.subtract(p1);
        if (intersectQuad(p1, dir, 2, pl1, pl2)) return true;
        if (intersectQuad(p1, dir, 1, pl1, pl3)) return true;
        if (intersectQuad(p1, dir, 0, pl1, pl4)) return true;
        if (intersectQuad(p1, dir, 0, pl2, pl3)) return true;
        if (intersectQuad(p1, dir, 1, pl2, pl4)) return true;
        if (intersectQuad(p1, dir, 2, pl3, pl4)) return true;
        return false;
    }
    
    private boolean intersectQuad(Vector3D line, Vector3D dir, int normal, Vector3D plane1, Vector3D plane2) {
        double den = dir.getComponent(normal);
        if ((den < 0.1) && (den > -0.1)) return false;
        double lambda = plane1.subtract(line).getComponent(normal) / den;
        if ((lambda < 0.0) || (lambda > 1.0)) return false;
        Vector3D point = line.add(dir.mult(lambda));
        for (int i=0; i<3; i++)
            if (i != normal) {
                double a = point.getComponent(i), b = plane1.getComponent(i), c = plane2.getComponent(i);
                if (((a < b) && (a < c)) || ((a > b) && (a > c))) return false;
            }
        return true;
    }
    
    @Override
    public String toString() {
        DecimalFormat format = new DecimalFormat("0.000");
        return "BoundingBox (" + format.format(minx) + ", " + format.format(miny) +
                ", " + format.format(minz) + ") to (" + format.format(maxx) + ", " +
                format.format(maxy) + ", " + format.format(maxz) + ")";
    }
}

package de.kleppmann.maniation.geometry;

import java.util.List;

import de.kleppmann.maniation.dynamics.Body;
import de.kleppmann.maniation.maths.Vector3D;

/**
 * A triangle in a mesh in which the vertex positions may change, but
 * the triangle configurations stay constant.
 */
public class MeshTriangle {
    
    Body body;
    MeshVertex[] vertices;
    BoundingBox bbox;
    double[] centre;
    
    // Cached numbers describing the triangle, for collision computations.
    // a, b, c: corners of this triangle
    // n: unit normal of this triangle
    // d, e, f: corners of the triangle we are checking intersection against
    // m: unit normal of the other triangle
    private double a1, a2, a3, b1, b2, b3, c1, c2, c3, d1, d2, d3,
        e1, e2, e3, f1, f2, f3, ab1, ab2, ab3, ac1, ac2, ac3,
        de1, de2, de3, df1, df2, df3, n1, n2, n3, m1, m2, m3;

    public MeshTriangle(Body body, MeshVertex v1, MeshVertex v2, MeshVertex v3) {
        this.body = body;
        vertices = new MeshVertex[3];
        vertices[0] = v1;
        vertices[1] = v2;
        vertices[2] = v3;
        centre = new double[3];
        updateBBox();
    }
    
    public MeshVertex[] getVertices() {
        return vertices;
    }
    
    public Body getBody() {
        return body;
    }
    
    void setBody(Body body) {
        this.body = body;
    }
    
    public Vector3D getNormal() {
        Vector3D direction = vertices[0].getNormal();
        direction = direction.add(vertices[1].getNormal());
        direction = direction.add(vertices[2].getNormal());
        Vector3D e1 = vertices[1].getPosition().subtract(vertices[0].getPosition());
        Vector3D e2 = vertices[2].getPosition().subtract(vertices[0].getPosition());
        Vector3D norm = e1.cross(e2).normalize();
        if (norm.mult(direction) < 0.0) norm = norm.mult(-1.0);
        return norm;
    }
    
    public void updateBBox() {
        bbox = new BoundingBox(
            vertices[0].max3(vertices[1], vertices[2], MeshVertex.Component.X),
            vertices[0].min3(vertices[1], vertices[2], MeshVertex.Component.X),
            vertices[0].max3(vertices[1], vertices[2], MeshVertex.Component.Y),
            vertices[0].min3(vertices[1], vertices[2], MeshVertex.Component.Y),
            vertices[0].max3(vertices[1], vertices[2], MeshVertex.Component.Z),
            vertices[0].min3(vertices[1], vertices[2], MeshVertex.Component.Z));
        centre[0] = vertices[0].avg3(vertices[1], vertices[2], MeshVertex.Component.X);
        centre[1] = vertices[0].avg3(vertices[1], vertices[2], MeshVertex.Component.Y);
        centre[2] = vertices[0].avg3(vertices[1], vertices[2], MeshVertex.Component.Z);
    }

    public void intersect(MeshTriangle other, Collision result) {
        updateNumbers(other);
        other.updateNumbers(this);
        // Do all points of the other triangle lie on the same side of the
        // plane of this triangle?
        int i = 0;
        if ((d1 - a1)*n1 + (d2 - a2)*n2 + (d3 - a3)*n3 >= 0.0) i++;
        if ((e1 - a1)*n1 + (e2 - a2)*n2 + (e3 - a3)*n3 >= 0.0) i++;
        if ((f1 - a1)*n1 + (f2 - a2)*n2 + (f3 - a3)*n3 >= 0.0) i++;
        if ((i == 0) || (i == 3)) return; // Yes --> no intersection
        // Yes. Now, do all points of this triangle lie on the
        // same side of the plane of the other triangle?
        i = 0;
        if ((a1 - d1)*m1 + (a2 - d2)*m2 + (a3 - d3)*m3 >= 0.0) i++;
        if ((b1 - d1)*m1 + (b2 - d2)*m2 + (b3 - d3)*m3 >= 0.0) i++;
        if ((c1 - d1)*m1 + (c2 - d2)*m2 + (c3 - d3)*m3 >= 0.0) i++;
        if ((i == 0) || (i == 3)) return; // Yes --> no intersection
        // Intersect edges of def with triangle abc
        Vector3D p1 = lineAgainstTriangle(d1, d2, d3, e1, e2, e3);
        Vector3D p2 = lineAgainstTriangle(e1, e2, e3, f1, f2, f3);
        Vector3D p3 = lineAgainstTriangle(f1, f2, f3, d1, d2, d3);
        // Intersect edges of abc with triangle def
        Vector3D p4 = other.lineAgainstTriangle(a1, a2, a3, b1, b2, b3);
        Vector3D p5 = other.lineAgainstTriangle(b1, b2, b3, c1, c2, c3);
        Vector3D p6 = other.lineAgainstTriangle(c1, c2, c3, a1, a2, a3);
        if ((p1 == null) && (p2 == null) && (p3 == null) && (p4 == null) &&
                (p5 == null) && (p6 == null)) return; // no intersection
        // Search for the pair of intersection points with the greatest distance
        List<Vector3D> points = new java.util.ArrayList<Vector3D>();
        if (p1 != null) points.add(p1); if (p2 != null) points.add(p2);
        if (p3 != null) points.add(p3); if (p4 != null) points.add(p4);
        if (p5 != null) points.add(p5); if (p6 != null) points.add(p6);
        if (points.size() == 1) points.add(points.get(0));
        Vector3D a = null, b = null; double max = -1e20;
        for (i=0; i<points.size()-1; i++)
            for (int j=i+1; j<points.size(); j++) {
                double d = points.get(j).subtract(points.get(i)).magnitude();
                if (d > max) {
                    a = points.get(i); b = points.get(j); max = d;
                }
            }
        result.addIntersection(a, b, this, other);
    }
    
    private void updateNumbers(MeshTriangle other) {
        // a, b and c are the three corners of the triangle
        a1 = vertices[0].getComponent(MeshVertex.Component.X);
        a2 = vertices[0].getComponent(MeshVertex.Component.Y);
        a3 = vertices[0].getComponent(MeshVertex.Component.Z);
        b1 = vertices[1].getComponent(MeshVertex.Component.X);
        b2 = vertices[1].getComponent(MeshVertex.Component.Y);
        b3 = vertices[1].getComponent(MeshVertex.Component.Z);
        c1 = vertices[2].getComponent(MeshVertex.Component.X);
        c2 = vertices[2].getComponent(MeshVertex.Component.Y);
        c3 = vertices[2].getComponent(MeshVertex.Component.Z);
        // Vectors from a to b, and from a to c
        ab1 = b1 - a1; ab2 = b2 - a2; ab3 = b3 - a3;
        ac1 = c1 - a1; ac2 = c2 - a2; ac3 = c3 - a3;
        // n is the unit normal of the triangle
        n1 = ab2*ac3 - ab3*ac2; n2 = ab3*ac1 - ab1*ac3; n3 = ab1*ac2 - ab2*ac1;
        double nm = Math.sqrt(n1*n1 + n2*n2 + n3*n3);
        assert(nm > 1e-12);
        n1 /= nm; n2 /= nm; n3 /= nm;
        // d, e and f are the corners of the other triangle
        d1 = other.vertices[0].getComponent(MeshVertex.Component.X);
        d2 = other.vertices[0].getComponent(MeshVertex.Component.Y);
        d3 = other.vertices[0].getComponent(MeshVertex.Component.Z);
        e1 = other.vertices[1].getComponent(MeshVertex.Component.X);
        e2 = other.vertices[1].getComponent(MeshVertex.Component.Y);
        e3 = other.vertices[1].getComponent(MeshVertex.Component.Z);
        f1 = other.vertices[2].getComponent(MeshVertex.Component.X);
        f2 = other.vertices[2].getComponent(MeshVertex.Component.Y);
        f3 = other.vertices[2].getComponent(MeshVertex.Component.Z);
        // Vectors from d to e, and from d to f
        de1 = e1 - d1; de2 = e2 - d2; de3 = e3 - d3;
        df1 = f1 - d1; df2 = f2 - d2; df3 = f3 - d3;
        // m is the unit normal of the other triangle
        m1 = de2*df3 - de3*df2; m2 = de3*df1 - de1*df3; m3 = de1*df2 - de2*df1;
        double mm = Math.sqrt(n1*n1 + n2*n2 + n3*n3);
        assert(mm > 1e-12);
        m1 /= mm; m2 /= mm; m3 /= mm;
    }
    
    /**
     * Checks whether a straight line, specified by its two endpoints, intersects
     * this triangle (abc).
     * (p1, p2, p3) is the beginning and (q1, q2, q3) the endpoint of the line.
     * @return Point vector of the intersection point if they intersect, null otherwise.
     */
    private Vector3D lineAgainstTriangle(double p1, double p2, double p3,
            double q1, double q2, double q3) {
        // Intersect line with plane
        double t = (q1 - p1)*n1 + (q2 - p2)*n2 + (q3 - p3)*n3;
        if ((t < 1e-10) && (t > -1e-10)) return null;
        t = ((a1 - p1)*n1 + (a2 - p2)*n2 + (a3 - p3)*n3) / t;
        if ((t > 1.0) || (t < 0.0)) return null;
        // We now know that the intersection lies between p and q.
        // r is the point of intersection with the plane:
        double r1 = p1 + t*(q1 - p1), r2 = p2 + t*(q2 - p2), r3 = p3 + t*(q3 - p3);
        // v is orthogonal to ab and also lies in the plane of the triangle:
        double v1 = ab2*n3 - ab3*n2, v2 = ab3*n1 - ab1*n3, v3 = ab1*n2 - ab2*n1;
        // w is orthogonal to ac and also lies in the plane of the triangle:
        double w1 = ac2*n3 - ac3*n2, w2 = ac3*n1 - ac1*n3, w3 = ac1*n2 - ac2*n1;
        // Now check that r is in the triangle.
        double x = ((r1 - a1)*v1 + (r2 - a2)*v2 + (r3 - a3)*v3) / (ac1*v1 + ac2*v2 + ac3*v3);
        double y = ((r1 - a1)*w1 + (r2 - a2)*w2 + (r3 - a3)*w3) / (ab1*w1 + ab2*w2 + ab3*w3);
        if ((x < 0.0) || (y < 0.0) || (x + y > 1.0)) return null;
        return new Vector3D(r1, r2, r3);
    }
}

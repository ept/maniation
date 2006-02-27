package de.kleppmann.maniation.geometry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.kleppmann.maniation.dynamics.EdgeEdgeCollision;
import de.kleppmann.maniation.dynamics.InteractionList;
import de.kleppmann.maniation.dynamics.RigidBody;
import de.kleppmann.maniation.dynamics.VertexFaceCollision;
import de.kleppmann.maniation.maths.Vector3D;
import de.realityinabox.util.CommutativePair;

public class Collision {

    private List<CollisionPoint> intersections = new java.util.ArrayList<CollisionPoint>();
    private RigidBody body1, body2;
    private Set<MeshTriangle> body1Triangles, body2Triangles;
    private Vector3D planePoint = null, planeNormal = null, penetrationPoint = null;
    private MeshVertex vfVertex = null;
    private MeshTriangle vfFace = null;
    private Vector3D vfNormal = null;
    private CommutativePair<MeshVertex> body1Edge = null, body2Edge = null;
    
    public void addIntersection(Vector3D lineFrom, Vector3D lineTo, MeshTriangle tri1, MeshTriangle tri2) {
        intersections.add(new CollisionPoint(lineFrom, lineTo, tri1, tri2));
    }
    
    public boolean isColliding() {
        return intersections.size() > 0;
    }
    
    public void process(InteractionList result) {
        if (!isColliding()) return;
        // Find the two bodies that are actually colliding here
        Set<RigidBody> bodies = new HashSet<RigidBody>();
        for (CollisionPoint coll : intersections) {
            bodies.add(coll.tri1.getBody());
            bodies.add(coll.tri2.getBody());
        }
        if (bodies.size() != 2) throw new IllegalStateException();
        int i = 0;
        for (RigidBody body : bodies) {
            if (i == 0) body1 = body; else body2 = body;
            i++;
        }
        // Find the sets of intersected triangles for each body
        body1Triangles = new HashSet<MeshTriangle>();
        body2Triangles = new HashSet<MeshTriangle>();
        for (CollisionPoint coll : intersections) {
            if (coll.tri1.getBody() == body1) body1Triangles.add(coll.tri1);
            else body2Triangles.add(coll.tri1);
            if (coll.tri2.getBody() == body1) body1Triangles.add(coll.tri2);
            else body2Triangles.add(coll.tri2);
        }
        // Test if we can determine a particular type of collision; if not, just
        // approximate the lot with a plane.
        if (detectVertexFace()) {
            RigidBody vertexBody = (vfFace.getBody() == body1) ? body2 : body1;
            result.addInteraction(new VertexFaceCollision(vertexBody, vfVertex.getPosition(),
                    vfFace.getBody(), vfFace.getVertices()[0].getPosition(), vfNormal));
        } else if (detectEdgeEdge(body1Triangles) && detectEdgeEdge(body2Triangles)) {
            Vector3D p1 = body1Edge.getLeft().getPosition(), p2 = body2Edge.getLeft().getPosition();
            Vector3D d1 = body1Edge.getRight().getPosition().subtract(p1);
            Vector3D d2 = body2Edge.getRight().getPosition().subtract(p2);
            if (d1.cross(d2).mult(p2.subtract(p1)) > 0.0) d1 = d1.mult(-1.0);
            result.addInteraction(new EdgeEdgeCollision(body1, p1, d1, body2, p2, d2));
        } else {
            calcPlane();
        }
    }
    
    private void calcPlane() {
        // Find a point on the plane by averaging all collision line endpoints
        planePoint = new Vector3D();
        for (CollisionPoint coll : intersections) {
            planePoint = planePoint.add(coll.lineFrom).add(coll.lineTo);
        }
        planePoint = planePoint.mult(0.5/intersections.size());
        // Find the plane normal by averaging the normals of all triangles
        planeNormal = new Vector3D();
        for (CollisionPoint coll : intersections) {
            Vector3D n = coll.lineFrom.subtract(planePoint).cross(coll.lineFrom.subtract(planePoint));
            if (n.magnitude() > 1e-6) {
                // Flip this normal if it's pointing in the opposite direction to the previous ones
                if (planeNormal.mult(n) < 0.0) n = n.mult(-1.0);
                planeNormal = planeNormal.add(n);
            }
        }
        planeNormal = planeNormal.normalize();
        // Find the relative velocity (body2 minus body1) of the bodies at the plane
        Vector3D r1 = planePoint.subtract(body1.getCoMPosition());
        Vector3D r2 = planePoint.subtract(body1.getCoMPosition());
        Vector3D v1 = body1.getAngularVelocity().cross(r1).add(body1.getCoMVelocity());
        Vector3D v2 = body2.getAngularVelocity().cross(r2).add(body2.getCoMVelocity());
        double vsign = (v2.subtract(v1).mult(planeNormal) < 0.0) ? -1.0 : +1.0;
        // Find the point of maximum penetration through the plane
        double dmax = -1e20;
        for (CollisionPoint coll : intersections) {
            double bsign = (coll.tri1.getBody() == body1) ? -vsign : vsign;
            for (MeshVertex v : coll.tri1.getVertices()) {
                double dist = bsign*v.getPosition().subtract(planePoint).mult(planeNormal);
                if (dist > dmax) {
                    penetrationPoint = v.getPosition(); dmax = dist;
                }
            }
        }
        // The sign of the plane normal is chosen arbitrarily. Flip it such that the
        // point of maximum penetration lies on the forbidden side.
        if (penetrationPoint.subtract(planePoint).mult(planeNormal) > 0.0) {
            planeNormal = planeNormal.mult(-1.0);
        }
    }
    
    private boolean detectVertexFace() {
        if (body1Triangles.size() == 1) {
            // Only a single triangle of body 1 is intersected
            for (MeshTriangle tri : body1Triangles) vfFace = tri;
            // Find a vertex in body 2's triangles which is common to all intersected triangles
            Set<MeshVertex> vset = null;
            for (CollisionPoint coll : intersections) {
                Set<MeshVertex> verts = new HashSet<MeshVertex>();
                for (MeshVertex v : coll.tri2.getVertices()) verts.add(v);
                if (vset == null) vset = verts; else vset.retainAll(verts);
            }
            if (vset.size() != 1) return false;
            for (MeshVertex v : vset) vfVertex = v;
        } else if (body2Triangles.size() == 1) {
            // Only a single triangle of body 2 is intersected
            for (MeshTriangle tri : body2Triangles) vfFace = tri;
            // Find a vertex in body 1's triangles which is common to all intersected triangles
            Set<MeshVertex> vset = null;
            for (CollisionPoint coll : intersections) {
                Set<MeshVertex> verts = new HashSet<MeshVertex>();
                for (MeshVertex v : coll.tri2.getVertices()) verts.add(v);
                if (vset == null) vset = verts; else vset.retainAll(verts);
            }
            if (vset.size() != 1) return false;
            for (MeshVertex v : vset) vfVertex = v;
        } else return false;
        // Determine normal of the face
        Vector3D v1 = vfFace.getVertices()[0].getPosition();
        Vector3D v2 = vfFace.getVertices()[1].getPosition();
        Vector3D v3 = vfFace.getVertices()[2].getPosition();
        vfNormal = v2.subtract(v1).cross(v3.subtract(v1)).normalize();
        if (vfVertex.getPosition().subtract(v1).mult(vfNormal) > 0.0) vfNormal = vfNormal.mult(-1.0);
        return true;
    }
    
    private boolean detectEdgeEdge(Set<MeshTriangle> triangles) {
        Set<CommutativePair<MeshVertex>> edges = null;
        for (MeshTriangle tri : triangles) {
            Set<CommutativePair<MeshVertex>> triEdges = new HashSet<CommutativePair<MeshVertex>>();
            triEdges.add(new CommutativePair<MeshVertex>(tri.getVertices()[0], tri.getVertices()[1]));
            triEdges.add(new CommutativePair<MeshVertex>(tri.getVertices()[1], tri.getVertices()[2]));
            triEdges.add(new CommutativePair<MeshVertex>(tri.getVertices()[2], tri.getVertices()[0]));
            if (edges == null) edges = triEdges; else edges.retainAll(triEdges);
        }
        if (edges.size() != 1) return false;
        CommutativePair<MeshVertex> edge = null;
        for (CommutativePair<MeshVertex> e : edges) edge = e;
        if (triangles == body1Triangles) body1Edge = edge; else body2Edge = edge;
        return true;
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

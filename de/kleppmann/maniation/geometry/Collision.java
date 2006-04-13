package de.kleppmann.maniation.geometry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.kleppmann.maniation.dynamics.ArticulatedBody;
import de.kleppmann.maniation.dynamics.EdgeEdgeCollision;
import de.kleppmann.maniation.dynamics.InteractionList;
import de.kleppmann.maniation.dynamics.Body;
import de.kleppmann.maniation.dynamics.VertexFaceCollision;
import de.kleppmann.maniation.maths.Vector3D;
import de.realityinabox.util.CommutativePair;

public class Collision {

    private final List<CollisionPoint> intersections = new java.util.ArrayList<CollisionPoint>();
    //private final AnimateMesh mesh1, mesh2;
    private final Body body1, body2;
    private Set<MeshTriangle> body1Triangles, body2Triangles;
    private Vector3D planePoint = null, planeNormal = null, penetrationPoint = null;
    private Body planeBody = null;
    private MeshVertex vfVertex = null;
    private MeshTriangle vfFace = null;
    private Vector3D vfNormal = null;
    private CommutativePair<MeshVertex> body1Edge = null, body2Edge = null;
    
    public Collision(AnimateMesh mesh1, AnimateMesh mesh2) {
        //this.mesh1 = mesh1; this.mesh2 = mesh2;
        try {
            this.body1 = (Body) mesh1.getDynamicBody();
            this.body2 = (Body) mesh2.getDynamicBody();
        } catch (ClassCastException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public void addIntersection(Vector3D lineFrom, Vector3D lineTo, MeshTriangle tri1, MeshTriangle tri2) {
        intersections.add(new CollisionPoint(lineFrom, lineTo, tri1, tri2));
    }
    
    public boolean isColliding() {
        return intersections.size() > 0;
    }
    
    public void process(InteractionList result) {
        if (!isColliding()) return;
        // Sanity check the two colliding bodies
        for (CollisionPoint coll : intersections) {
            if (!(((coll.tri1.getBody() == body1) && (coll.tri2.getBody() == body2)) ||
                  ((coll.tri1.getBody() == body2) && (coll.tri2.getBody() == body1))))
                throw new IllegalStateException();
        }
        // Find the sets of intersected triangles for each body
        body1Triangles = new HashSet<MeshTriangle>();
        body2Triangles = new HashSet<MeshTriangle>();
        for (CollisionPoint coll : intersections) {
            body1Triangles.add(coll.getTri1());
            body2Triangles.add(coll.getTri2());
        }
        // Test if we can determine a particular type of collision; if not, just
        // approximate the lot with a plane.
        if (detectVertexFace()) {
            Body vertexBody = (vfFace.getBody() == body1) ? body2 : body1;
            result.addInteraction(new VertexFaceCollision(vertexBody, vfVertex.getPosition(),
                    vfFace.getBody(), vfFace.getVertices()[0].getPosition(), vfNormal));
        } else if (detectEdgeEdge(body1Triangles) && detectEdgeEdge(body2Triangles)) {
            Vector3D p1 = body1Edge.getLeft().getPosition(), p2 = body2Edge.getLeft().getPosition();
            Vector3D d1 = body1Edge.getRight().getPosition().subtract(p1);
            Vector3D d2 = body2Edge.getRight().getPosition().subtract(p2);
            if (d1.cross(d2).mult(p2.subtract(p1)) > 0.0) d1 = d1.mult(-1.0);
            result.addInteraction(new EdgeEdgeCollision(body1, p1, d1, body2, p2, d2));
        } else if (calcPlane()) {
            Body vertexBody = (planeBody == body1) ? body2 : body1;
            result.addInteraction(new VertexFaceCollision(vertexBody, penetrationPoint,
                    planeBody, planePoint, planeNormal));
        }
    }
    
    private boolean calcPlane() {
        // Find a point on the plane by averaging all collision line endpoints
        Set<CommutativePair<InexactPoint>> lines = new java.util.HashSet<CommutativePair<InexactPoint>>();
        for (CollisionPoint coll : intersections) {
            lines.add(new CommutativePair<InexactPoint>(new InexactPoint(coll.lineFrom),
                    new InexactPoint(coll.lineTo)));
        }
        planePoint = new Vector3D();
        for (CommutativePair<InexactPoint> line : lines) {
            planePoint = planePoint.add(line.getLeft ().getPosition());
            planePoint = planePoint.add(line.getRight().getPosition());
        }
        planePoint = planePoint.mult(0.5/lines.size());
        // Find the plane normal by averaging the normals (as specified in the file) of all triangles
        planeNormal = new Vector3D();
        for (CollisionPoint coll : intersections) {
            planeNormal = planeNormal.add(coll.tri1.getNormal()).subtract(coll.tri2.getNormal());
        }
        if (planeNormal.magnitude() < 1e-6) return false;
        planeNormal = planeNormal.normalize();
        // Nasty hack: if the collision is between an articulated body and a mesh body, the mesh
        // body should carry the plane.
        double dist = 1e20;
        if (body2 instanceof ArticulatedBody.Limb) {
            planeBody = body1;
            for (CollisionPoint coll : intersections) {
                for (MeshVertex v : coll.getTri2().getVertices()) {
                    double d = v.getPosition().subtract(planePoint).mult(planeNormal);
                    if (d < dist) dist = d;
                }
            }
        } else if (body1 instanceof ArticulatedBody.Limb) {
            planeBody = body2;
            planeNormal = planeNormal.mult(-1.0);
            for (CollisionPoint coll : intersections) {
                for (MeshVertex v : coll.getTri1().getVertices()) {
                    double d = v.getPosition().subtract(planePoint).mult(planeNormal);
                    if (d < dist) dist = d;
                }
            }
        } else {
            planeBody = body1;
            for (CollisionPoint coll : intersections) {
                for (MeshVertex v : coll.getTri1().getVertices()) {
                    double d = -v.getPosition().subtract(planePoint).mult(planeNormal);
                    if (d < dist) dist = d;
                }
                for (MeshVertex v : coll.getTri2().getVertices()) {
                    double d = v.getPosition().subtract(planePoint).mult(planeNormal);
                    if (d < dist) dist = d;
                }
            }
        }
        // Find the point of maximum penetration through the plane
        /*for (CollisionPoint coll : intersections) {
            for (MeshVertex v : coll.getTri1().getVertices()) {
                double d = -v.getPosition().subtract(planePoint).mult(planeNormal);
                if (d < dist) dist = d;
            }
            for (MeshVertex v : coll.getTri2().getVertices()) {
                double d = v.getPosition().subtract(planePoint).mult(planeNormal);
                if (d < dist) dist = d;
            }
        }*/
        // Find the plane normal by averaging the normals of all triangles
        /*planeNormal = new Vector3D();
        for (CommutativePair<InexactPoint> line : lines) {
            Vector3D a = line.getLeft ().getPosition().subtract(planePoint);
            Vector3D b = line.getRight().getPosition().subtract(planePoint);
            Vector3D n = a.cross(b);
            if (n.magnitude() > 1e-6) {
                // Flip this normal if it's pointing in the opposite direction to the previous ones
                if (planeNormal.mult(n) < 0.0) n = n.mult(-1.0);
                planeNormal = planeNormal.add(n);
            }
        }
        // If the plane is so undefined that we can't even make out a normal, we might as well
        // just ignore the collision. (This might happen in a vertex/vertex collision. Maybe it
        // would be better to handle this case separately.)
        if (planeNormal.magnitude() < 1e-8) return false;
        planeNormal = planeNormal.normalize();
        // Find the point of maximum penetration through the plane
        double dmax = -1e20, dmin = 1e20;
        for (CollisionPoint coll : intersections) {
            for (MeshVertex v : coll.getTri1().getVertices()) {
                double dist = -v.getPosition().subtract(planePoint).mult(planeNormal);
                if (dist > dmax) dmax = dist;
                if (dist < dmin) dmin = dist;
            }
            for (MeshVertex v : coll.getTri2().getVertices()) {
                double dist = v.getPosition().subtract(planePoint).mult(planeNormal);
                if (dist > dmax) dmax = dist;
                if (dist < dmin) dmin = dist;
            }
        }
        // Try to determine which side of the plane is the 'bad' one by considering the
        // locations of the centres of masses relative to the plane.
        double dist;
        Body.State state1, state2;
        try {
            state1 = (Body.State) mesh1.getDynamicState();
            state2 = (Body.State) mesh2.getDynamicState();
        } catch (ClassCastException e) {
            throw new IllegalStateException(e);
        }
        double com = state2.getCoMPosition().subtract(state1.getCoMPosition()).mult(planeNormal);
        if (com > 0.0) {
            planeBody = body1; dist = dmin;
        } else {
            planeBody = body2; dist = -dmax;
        }*/
        // As penetration point, we just choose one offset from the plane centrepoint
        // by the appropriate distance.
        penetrationPoint = planePoint.add(planeNormal.mult(dist));
        return true;
    }
    
    private boolean detectVertexFace() {
        if (body1Triangles.size() == 1) {
            // Only a single triangle of body 1 is intersected
            for (MeshTriangle tri : body1Triangles) vfFace = tri;
            // Find a vertex in body 2's triangles which is common to all intersected triangles
            Set<MeshVertex> vset = null;
            for (CollisionPoint coll : intersections) {
                Set<MeshVertex> verts = new HashSet<MeshVertex>();
                for (MeshVertex v : coll.getTri2().getVertices()) verts.add(v);
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
                for (MeshVertex v : coll.getTri1().getVertices()) verts.add(v);
                if (vset == null) vset = verts; else vset.retainAll(verts);
            }
            if (vset.size() != 1) return false;
            for (MeshVertex v : vset) vfVertex = v;
        } else return false;
        // Determine normal of the face
        /*Vector3D v1 = vfFace.getVertices()[0].getPosition();
        Vector3D v2 = vfFace.getVertices()[1].getPosition();
        Vector3D v3 = vfFace.getVertices()[2].getPosition();
        vfNormal = v2.subtract(v1).cross(v3.subtract(v1)).normalize();
        if (vfVertex.getPosition().subtract(v1).mult(vfNormal) > 0.0) vfNormal = vfNormal.mult(-1.0);*/
        vfNormal = vfFace.getNormal();
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
        private Vector3D lineFrom, lineTo;
        private MeshTriangle tri1, tri2;
        
        CollisionPoint(Vector3D lineFrom, Vector3D lineTo, MeshTriangle tri1, MeshTriangle tri2) {
            this.lineFrom = lineFrom; this.lineTo = lineTo;
            this.tri1 = tri1; this.tri2 = tri2;
        }
        
        MeshTriangle getTri1() {
            return (tri1.getBody() == body1) ? tri1 : tri2;
        }

        MeshTriangle getTri2() {
            return (tri1.getBody() == body2) ? tri1 : tri2;
        }
    }
}

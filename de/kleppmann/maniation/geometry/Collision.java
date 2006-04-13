package de.kleppmann.maniation.geometry;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.kleppmann.maniation.dynamics.EdgeEdgeCollision;
import de.kleppmann.maniation.dynamics.InteractionList;
import de.kleppmann.maniation.dynamics.Body;
import de.kleppmann.maniation.dynamics.VertexFaceCollision;
import de.kleppmann.maniation.maths.Vector3D;
import de.realityinabox.util.CommutativePair;
import de.realityinabox.util.Pair;

public class Collision {

    private final List<CollisionPoint> intersections = new java.util.ArrayList<CollisionPoint>();
    private final AnimateMesh mesh1, mesh2;
    private final Body body1, body2;
    private Set<MeshTriangle> body1Triangles, body2Triangles;
    private Body planeBody = null;
    private MeshVertex vfVertex = null;
    private MeshTriangle vfFace = null;
    private Vector3D vfNormal = null;
    private CommutativePair<MeshVertex> body1Edge = null, body2Edge = null;
    
    public Collision(AnimateMesh mesh1, AnimateMesh mesh2) {
        this.mesh1 = mesh1; this.mesh2 = mesh2;
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
        } else compoundContact(result);
    }
    
    private void compoundContact(InteractionList result) {
        // The more coarse mesh will be used for the faces, to minimize the number of constraints
        Set<MeshTriangle> planeTriangles, vertexTriangles;
        Body vertexBody; AnimateMesh planeMesh;
        if (body1Triangles.size() < body2Triangles.size()) {
            planeBody = body1; vertexBody = body2; planeMesh = mesh1;
            planeTriangles = body1Triangles; vertexTriangles = body2Triangles;
        } else {
            planeBody = body2; vertexBody = body1; planeMesh = mesh2;
            planeTriangles = body2Triangles; vertexTriangles = body1Triangles;
        }
        // Determine which of the other mesh's vertices lie inside the face mesh volume
        Set<MeshVertex> inside = new java.util.HashSet<MeshVertex>();
        Set<MeshVertex> outside = new java.util.HashSet<MeshVertex>();
        for (MeshTriangle tri : vertexTriangles) {
            for (MeshVertex vert : tri.vertices) {
                if (inside.contains(vert) || outside.contains(vert)) continue;
                if (planeMesh.isInsideVolume(vert.getPosition())) inside.add(vert);
                else outside.add(vert);
            }
        }
        // Associate each penetrated vertex with the face closest to it.
        // But for each face, choose the furthest associated vertex.
        Map<MeshTriangle, VertexFaceCollision> faceColl = new java.util.HashMap<MeshTriangle, VertexFaceCollision>();
        for (MeshVertex vert : inside) {
            VertexFaceCollision vfmax = null; double pmax = 0.0; MeshTriangle mtmax = null;
            for (MeshTriangle face : planeTriangles) {
                VertexFaceCollision vf = new VertexFaceCollision(vertexBody, vert.getPosition(),
                        planeBody, face.getVertices()[0].getPosition(), face.getNormal());
                double pen = vf.getPenalty().getComponent(0);
                if (((vfmax == null) || (pen > pmax)) && (pen < 0.0)) {
                    vfmax = vf; pmax = pen; mtmax = face;
                }
            }
            VertexFaceCollision prev = faceColl.get(mtmax);
            if ((vfmax != null) && ((prev == null) || (prev.getPenalty().getComponent(0) > pmax)))
                faceColl.put(mtmax, vfmax);
        }
        for (Map.Entry<MeshTriangle, VertexFaceCollision> entry : faceColl.entrySet()) {
            result.addInteraction(entry.getValue());
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
    
    private boolean linesProximity(Vector3D p1, Vector3D p2, Vector3D q1, Vector3D q2) {
        Vector3D s = p2.subtract(p1), t = q2.subtract(q1);
        double ss = s.magnitude()*s.magnitude(), tt = t.magnitude()*t.magnitude();
        double st = s.mult(t);
        double den = ss*tt - st*st;
        if ((den < 1e-10) && (den > -1e-10)) return false;
        Vector3D v = s.mult(st).add(t.mult(ss));
        double rho = p1.subtract(q1).mult(v) / den;
        if ((rho < 0.0) || (rho > 1.0)) return false;
        double lambda = s.mult(t.mult(rho).add(q1).subtract(p1)) / ss;
        return (lambda >= 0.0) && (lambda <= 1.0);
    }
    
    // Returns the edges in anticlockwise orientation, as seen when looking at the outside
    private Set<Pair<MeshVertex,MeshVertex>> triangleEdges(MeshTriangle tri) {
        Set<Pair<MeshVertex,MeshVertex>> result = new java.util.HashSet<Pair<MeshVertex,MeshVertex>>();
        Vector3D p1 = tri.getVertices()[0].getPosition();
        Vector3D p2 = tri.getVertices()[1].getPosition();
        Vector3D p3 = tri.getVertices()[2].getPosition();
        if (p2.subtract(p1).cross(p3.subtract(p1)).mult(tri.getNormal()) >= 0.0) {
            result.add(new Pair<MeshVertex,MeshVertex>(tri.getVertices()[0], tri.getVertices()[1]));
            result.add(new Pair<MeshVertex,MeshVertex>(tri.getVertices()[1], tri.getVertices()[2]));
            result.add(new Pair<MeshVertex,MeshVertex>(tri.getVertices()[2], tri.getVertices()[0]));
        } else {
            result.add(new Pair<MeshVertex,MeshVertex>(tri.getVertices()[0], tri.getVertices()[2]));
            result.add(new Pair<MeshVertex,MeshVertex>(tri.getVertices()[2], tri.getVertices()[1]));
            result.add(new Pair<MeshVertex,MeshVertex>(tri.getVertices()[1], tri.getVertices()[0]));
        }
        return result;
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

package de.kleppmann.maniation.geometry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.kleppmann.maniation.dynamics.EdgeEdgeCollision;
import de.kleppmann.maniation.dynamics.InequalityConstraint;
import de.kleppmann.maniation.dynamics.InteractionList;
import de.kleppmann.maniation.dynamics.Body;
import de.kleppmann.maniation.dynamics.Simulation;
import de.kleppmann.maniation.dynamics.SphereEdgeCollision;
import de.kleppmann.maniation.dynamics.SphereFaceCollision;
import de.kleppmann.maniation.dynamics.SphereSphereCollision;
import de.kleppmann.maniation.dynamics.VertexFaceCollision;
import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;
import de.realityinabox.util.CommutativePair;
import de.realityinabox.util.Pair;

public class Collision {

    private final List<CollisionPoint> intersections = new java.util.ArrayList<CollisionPoint>();
    private final AnimateMesh mesh1, mesh2;
    private final Body body1, body2;
    private Set<MeshTriangle> body1Triangles, body2Triangles;
    //private Body planeBody = null;
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
        if (detectLimbLimb(result)) return;
        if (detectLimbMesh(result)) return;
        if (detectVertexFace()) {
            Body vertexBody = (vfFace.getBody() == body1) ? body2 : body1;
            result.addInteraction(new VertexFaceCollision(vertexBody, vfVertex.getPosition(),
                    vfFace.getBody(), vfFace.getVertices()[0].getPosition(), vfNormal, Simulation.ELASTICITY));
        } else if (detectEdgeEdge(body1Triangles) && detectEdgeEdge(body2Triangles)) {
            Vector3D p1 = body1Edge.getLeft().getPosition(), p2 = body2Edge.getLeft().getPosition();
            Vector3D d1 = body1Edge.getRight().getPosition().subtract(p1);
            Vector3D d2 = body2Edge.getRight().getPosition().subtract(p2);
            if (d1.cross(d2).mult(p2.subtract(p1)) > 0.0) d1 = d1.mult(-1.0);
            result.addInteraction(new EdgeEdgeCollision(body1, p1, d1, body2, p2, d2, Simulation.ELASTICITY));
        } else compoundContact(result);
    }
    
    private void compoundContact(InteractionList result) {
        Vector3D planePoint, planeNormal;
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
        if (planeNormal.magnitude() < 1e-6) return;
        planeNormal = planeNormal.normalize();
        // Find the point of maximum penetration through the plane
        MeshVertex vert = null; Body vertexBody = null; double dist = 0.0;
        for (CollisionPoint coll : intersections) {
            for (MeshVertex v : coll.getTri1().getVertices()) {
                if (mesh2.isInsideVolume(v.getPosition())) {
                    double d = Math.abs(v.getPosition().subtract(planePoint).mult(planeNormal));
                    if (d > dist) {
                        vert = v; vertexBody = body1; dist = d;
                    }
                }
            }
            for (MeshVertex v : coll.getTri2().getVertices()) {
                if (mesh1.isInsideVolume(v.getPosition())) {
                    double d = Math.abs(v.getPosition().subtract(planePoint).mult(planeNormal));
                    if (d > dist) {
                        vert = v; vertexBody = body2; dist = d;
                    }
                }
            }
        }
        // Create vertex/face constraint using the plane
        if (vert != null) {
            VertexFaceCollision vf = new VertexFaceCollision(vertexBody, vert.getPosition(),
                    (vertexBody == body1 ? body2 : body1), planePoint, planeNormal, Simulation.ELASTICITY);
            result.addInteraction(vf);
        }
        // Find all edges in both bodies which have an intersected triangle on either side
        Set<Edge> body1Edges = busyEdges(body1Triangles), body2Edges = busyEdges(body2Triangles);
        // Pair them up and choose one to be an edge/edge constraint
        EdgeEdgeCollision ee = null; dist = 0.0;
        for (Edge edge1 : body1Edges) {
            Vector3D e1 = edge1.getLeft().getPosition();
            Vector3D e2 = edge1.getRight().getPosition();
            Vector3D dir1 = e2.subtract(e1);
            for (Edge edge2 : body2Edges) {
                Vector3D f1 = edge2.getLeft().getPosition();
                Vector3D f2 = edge2.getRight().getPosition();
                Vector3D dir2 = f2.subtract(f1);
                if (linesProximity(e1, e2, f1, f2)) {
                    Vector3D normal = dir1.cross(dir2).normalize();
                    Vector3D oneToTwo = edge1.firstTri.getNormal().add(edge1.secondTri.getNormal());
                    oneToTwo = oneToTwo.subtract(edge2.firstTri.getNormal()).subtract(edge2.secondTri.getNormal());
                    if (normal.mult(oneToTwo) < 0.0) {
                        normal = normal.mult(-1.0); dir2 = dir2.mult(-1.0);
                    }
                    double d = f1.subtract(e1).mult(normal);
                    if (d < dist) {
                        ee = new EdgeEdgeCollision(body1, e1, dir1, body2, f1, dir2, Simulation.ELASTICITY);
                        dist = d;
                    }
                }
            }
        }
        if (ee != null) result.addInteraction(ee);
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
    
    private boolean detectLimbLimb(InteractionList result) {
        if (!(mesh1 instanceof ArticulatedLimb)) return false;
        if (!(mesh2 instanceof ArticulatedLimb)) return false;
        ArticulatedLimb limb1 = (ArticulatedLimb) mesh1;
        ArticulatedLimb limb2 = (ArticulatedLimb) mesh2;
        Quaternion orient1 = limb1.getCurrentOrientation().getInverse();
        Quaternion orient2 = limb2.getCurrentOrientation().getInverse();
        Vector3D base1 = limb1.getDynamicState().getCoMPosition();
        Vector3D base2 = limb2.getDynamicState().getCoMPosition();
        SphereSphereCollision coll = null; double dist = 1e40; 
        for (Pair<Vector3D, Double> bubble1 : limb1.getBubbles()) {
            Vector3D centre1 = orient1.transform(bubble1.getLeft().subtract(base1));
            for (Pair<Vector3D, Double> bubble2 : limb2.getBubbles()) {
                Vector3D centre2 = orient2.transform(bubble2.getLeft().subtract(base2));
                double d = bubble2.getLeft().subtract(bubble1.getLeft()).magnitude() -
                        bubble1.getRight() - bubble2.getRight();
                if (d < dist) {
                    dist = d;
                    coll = new SphereSphereCollision(body1, centre1, bubble1.getRight(),
                            body2, centre2, bubble2.getRight(), Simulation.ELASTICITY);
                }
            }
        }
        if (coll == null) return false;
        result.addInteraction(coll);
        return true;
    }
    
    private boolean detectLimbMesh(InteractionList result) {
        ArticulatedLimb limb = null; AnimateMesh mesh = null; Body meshBody = null;
        Set<Edge> edges = null; Set<MeshTriangle> tris = null;
        if (mesh1 instanceof ArticulatedLimb) {
            limb = (ArticulatedLimb) mesh1; mesh = mesh2; meshBody = body2;
            edges = busyEdges(body2Triangles); tris = body2Triangles;
        } else if (mesh2 instanceof ArticulatedLimb) {
            limb = (ArticulatedLimb) mesh2; mesh = mesh1; meshBody = body1;
            edges = busyEdges(body1Triangles); tris = body1Triangles;
        }
        if (limb == null) return false;
        Quaternion orientLimb = limb.getCurrentOrientation().getInverse();
        Quaternion orientMesh = mesh.getCurrentOrientation().getInverse();
        Vector3D posLimb = limb.getDynamicState().getCoMPosition();
        Vector3D posMesh = ((Body.State) mesh.getDynamicState()).getCoMPosition();
        double dist = 1e20; InequalityConstraint constr = null;
        for (Pair<Vector3D, Double> bubble : limb.getBubbles()) {
            Vector3D centre = orientLimb.transform(bubble.getLeft().subtract(posLimb));
            double radius = bubble.getRight();
            for (MeshTriangle tri : tris) {
                Vector3D p1 = bubble.getLeft().add(tri.getNormal().mult(-1000));
                Vector3D p2 = bubble.getLeft().add(tri.getNormal().mult(+1000));
                Vector3D pointW = tri.getVertices()[0].getPosition();
                Vector3D pointL = orientMesh.transform(pointW.subtract(posMesh));
                double d = bubble.getLeft().subtract(pointW).mult(tri.getNormal()) - radius;
                if ((d > 0) || (d > dist)) continue;
                if (tri.lineAgainstTriangle(p1.getComponent(0), p1.getComponent(1), p1.getComponent(2),
                        p2.getComponent(0), p2.getComponent(1), p2.getComponent(2)) == null) continue;
                constr = new SphereFaceCollision(limb.getDynamicBody(), centre, radius,
                        meshBody, pointL, orientMesh.transform(tri.getNormal()), Simulation.ELASTICITY);
                dist = d;
            }
            for (Edge edge : edges) {
                Vector3D p1 = edge.getLeft().getPosition();
                Vector3D p2 = edge.getRight().getPosition();
                Vector3D dir = p2.subtract(p1).normalize();
                double d = bubble.getLeft().subtract(p1).cross(dir).magnitude() - radius;
                if ((d > 0) || (d > dist)) continue;
                constr = new SphereEdgeCollision(limb.getDynamicBody(), centre, radius, meshBody,
                        orientMesh.transform(p1.subtract(posMesh)), orientMesh.transform(dir),
                        Simulation.ELASTICITY);
                dist = d;
            }
        }
        if (constr == null) return false;
        result.addInteraction(constr);
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
    
    private Set<Edge> busyEdges(Set<MeshTriangle> triangles) {
        Set<Edge> once = new HashSet<Edge>(), twice = new HashSet<Edge>();
        for (MeshTriangle tri : triangles) {
            MeshVertex v1 = tri.getVertices()[0];
            MeshVertex v2 = tri.getVertices()[1];
            MeshVertex v3 = tri.getVertices()[2];
            Edge edge1 = new Edge(v1, v2, tri);
            Edge edge2 = new Edge(v2, v3, tri);
            Edge edge3 = new Edge(v3, v1, tri);
            if (once.contains(edge1)) {
                twice.add(edge1); edge1.secondTri = tri;
            } else once.add(edge1);
            if (once.contains(edge2)) {
                twice.add(edge2); edge2.secondTri = tri;
            } else once.add(edge2);
            if (once.contains(edge3)) {
                twice.add(edge3); edge3.secondTri = tri;
            } else once.add(edge3);
        }
        return twice;
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
    
    
    private static class Edge extends CommutativePair<MeshVertex> {
        private MeshTriangle firstTri, secondTri = null;
        
        Edge(MeshVertex v1, MeshVertex v2, MeshTriangle firstTri) {
            super(v1, v2);
            this.firstTri = firstTri;
        }
    }
}

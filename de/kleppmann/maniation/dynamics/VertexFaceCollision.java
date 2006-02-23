package de.kleppmann.maniation.dynamics;

import java.util.List;
import java.util.Map;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.MatrixImpl;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.maths.VectorImpl;

public class VertexFaceCollision implements InequalityConstraint {
    
    private RigidBody vertexBody, faceBody;
    private Vector3D vertex, facePoint, faceNormal;
    private double a1, a2, a3, b1, b2, b3, ad1, ad2, ad3, bd1, bd2, bd3, w1, w2, w3, p1, p2, p3,
        t1, t2, t3, n1, n2, n3;

    
    public VertexFaceCollision(RigidBody vertexBody, Vector3D vertex, RigidBody faceBody,
            Vector3D facePoint, Vector3D faceNormal) {
        this.vertexBody = vertexBody;
        this.vertex = vertex;
        this.faceBody = faceBody;
        this.facePoint = facePoint;
        this.faceNormal = faceNormal;
    }
    
    private void update() {
        a1 = faceBody.getCoMPosition().getComponent(0);
        a2 = faceBody.getCoMPosition().getComponent(1);
        a3 = faceBody.getCoMPosition().getComponent(2);
        b1 = vertexBody.getCoMPosition().getComponent(0);
        b2 = vertexBody.getCoMPosition().getComponent(1);
        b3 = vertexBody.getCoMPosition().getComponent(2);
        ad1 = faceBody.getCoMVelocity().getComponent(0);
        ad2 = faceBody.getCoMVelocity().getComponent(1);
        ad3 = faceBody.getCoMVelocity().getComponent(2);
        bd1 = vertexBody.getCoMVelocity().getComponent(0);
        bd2 = vertexBody.getCoMVelocity().getComponent(1);
        bd3 = vertexBody.getCoMVelocity().getComponent(2);
        w1 = faceBody.getAngularVelocity().getComponent(0);
        w2 = faceBody.getAngularVelocity().getComponent(1);
        w3 = faceBody.getAngularVelocity().getComponent(2);
        p1 = vertexBody.getAngularVelocity().getComponent(0);
        p2 = vertexBody.getAngularVelocity().getComponent(1);
        p3 = vertexBody.getAngularVelocity().getComponent(2);
        t1 = vertex.getComponent(0);
        t2 = vertex.getComponent(1);
        t3 = vertex.getComponent(2);
        n1 = faceNormal.getComponent(0);
        n2 = faceNormal.getComponent(0);
        n3 = faceNormal.getComponent(0);
    }

    public Vector getPenalty() {
        double[] v = {vertexBody.getCoMPosition().add(vertex).subtract(faceBody.getCoMPosition()).
            subtract(facePoint).mult(faceNormal)};
        return new VectorImpl(v);
    }

    public Vector getPenaltyDot() {
        update();
        double[] v = {n1*(bd1 - ad1 + p2*t3 - p3*t2) + n2*(bd2 - ad2 + p3*t1 - p1*t3) +
                n3*(bd3 - ad3 + p1*t2 - p2*t1) + (a1 - b1 - t1)*(n2*w3 - n3*w2) +
                (a2 - b2 - t2)*(n3*w1 - n1*w3) + (a3 - b3 - t3)*(n1*w2 - n2*w1) };
        return new VectorImpl(v);
    }

    public Map<GeneralizedBody, Matrix> getJacobian() {
        update();
        double x1 = a1 - b1 - t1, x2 = a2 - b2 - t2, x3 = a3 - b3 - t3;
        double[][] jf = {{ -n1, -n2, -n3, x2*n3 - x3*n2, x3*n1 - x1*n3, x1*n2 - x2*n1 }};
        double[][] jv = {{  n1,  n2,  n3, t2*n3 - t3*n2, t3*n1 - t1*n3, t1*n2 - t2*n1 }};
        Map<GeneralizedBody, Matrix> result = new java.util.HashMap<GeneralizedBody, Matrix>();
        result.put(faceBody, new MatrixImpl(jf));
        result.put(vertexBody, new MatrixImpl(jv));
        return result;
    }

    public Map<GeneralizedBody, Matrix> getJacobianDot() {
        double t6 = ad2-bd2-p3*t1+p1*t3;
        double t11 = ad3-bd3-p1*t2+p2*t1;
        double t14 = a2-b2-t2;
        double t17 = w1*n2-w2*n1;
        double t19 = a3-b3-t3;
        double t22 = -w3*n1+w1*n3;
        double t26 = ad1-bd1-p2*t3+p3*t2;
        double t31 = a1-b1-t1;
        double t36 = w2*n3-w3*n2;
        double[][] jf = {{ 0, 0, 0,
            2.0*t6*n3-2.0*t11*n2+t14*t17+t19*t22,
            -2.0*t26*n3+2.0*t11*n1-t31*t17+t19*t36,
            2.0*t26*n2-2.0*t6*n1-t31*t22-t14*t36 }};
        double t7 = p1*t2-p2*t1;
        double t10 = -p3*t1+p1*t3;
        double t16 = p2*t3-p3*t2;
        double[][] jv = {{ 0, 0, 0, -t7*n2-n3*t10, t7*n1-n3*t16, n1*t10+n2*t16 }};
        Map<GeneralizedBody, Matrix> result = new java.util.HashMap<GeneralizedBody, Matrix>();
        result.put(faceBody, new MatrixImpl(jf));
        result.put(vertexBody, new MatrixImpl(jv));
        return result;
    }

    public boolean isInequality() {
        return true;
    }

    public int getDimension() {
        return 1;
    }

    public List<SimulationObject> getObjects() {
        List<SimulationObject> result = new java.util.ArrayList<SimulationObject>();
        result.add(faceBody);
        result.add(vertexBody);
        return result;
    }
}

package de.kleppmann.maniation.dynamics;

import java.util.List;
import java.util.Map;

import de.kleppmann.maniation.dynamics.GeneralizedBody.State;
import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.MatrixImpl;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.maths.VectorImpl;

public class SphereFaceCollision implements InequalityConstraint {

    private final Body sphereBody, faceBody;
    private final Vector3D centre, point, normal;
    private final double radius;
    private Body.State sphereBodyState, faceBodyState;
    private double a1, a2, a3, b1, b2, b3, ad1, ad2, ad3, bd1, bd2, bd3, w1, w2, w3, p1, p2, p3,
        s1, s2, s3, t1, t2, t3, n1, n2, n3;
    private Map<GeneralizedBody, Matrix> jacMap, jacDotMap;
    
    // All vectors are given in local coordinates
    public SphereFaceCollision(Body sphereBody, Vector3D centre, double radius, 
            Body faceBody, Vector3D point, Vector3D normal) {
        this.sphereBody = sphereBody; this.faceBody = faceBody;
        this.centre = centre; this.point = point; this.normal = normal.normalize();
        this.radius = radius;
    }

    public boolean isInequality() {
        return true;
    }

    public Map<Body, Vector3D> setToZero() {
        return new java.util.HashMap<Body, Vector3D>();
    }

    public void setStateMapping(Map<GeneralizedBody, State> states) {
        jacMap = jacDotMap = null;
        try {
            sphereBodyState = (Body.State) states.get(sphereBody);
            faceBodyState = (Body.State) states.get(faceBody);
        } catch (ClassCastException e) {
            throw new IllegalStateException(e);
        }
        a1 = sphereBodyState.getCoMPosition().getComponent(0);
        a2 = sphereBodyState.getCoMPosition().getComponent(1);
        a3 = sphereBodyState.getCoMPosition().getComponent(2);
        b1 = faceBodyState.getCoMPosition().getComponent(0);
        b2 = faceBodyState.getCoMPosition().getComponent(1);
        b3 = faceBodyState.getCoMPosition().getComponent(2);
        ad1 = sphereBodyState.getCoMVelocity().getComponent(0);
        ad2 = sphereBodyState.getCoMVelocity().getComponent(1);
        ad3 = sphereBodyState.getCoMVelocity().getComponent(2);
        bd1 = faceBodyState.getCoMVelocity().getComponent(0);
        bd2 = faceBodyState.getCoMVelocity().getComponent(1);
        bd3 = faceBodyState.getCoMVelocity().getComponent(2);
        w1 = sphereBodyState.getAngularVelocity().getComponent(0);
        w2 = sphereBodyState.getAngularVelocity().getComponent(1);
        w3 = sphereBodyState.getAngularVelocity().getComponent(2);
        p1 = faceBodyState.getAngularVelocity().getComponent(0);
        p2 = faceBodyState.getAngularVelocity().getComponent(1);
        p3 = faceBodyState.getAngularVelocity().getComponent(2);
        Vector3D s = sphereBodyState.getOrientation().transform(centre);
        Vector3D t = faceBodyState.getOrientation().transform(point);
        Vector3D n = faceBodyState.getOrientation().transform(normal);
        s1 = s.getComponent(0);
        s2 = s.getComponent(1);
        s3 = s.getComponent(2);
        t1 = t.getComponent(0);
        t2 = t.getComponent(1);
        t3 = t.getComponent(2);
        n1 = n.getComponent(0);
        n2 = n.getComponent(1);
        n3 = n.getComponent(2);
    }

    public Vector getPenalty() {
        double[] v = {n1*(a1+s1-b1-t1) + n2*(a2+s2-b2-t2) + n3*(a3+s3-b3-t3) - radius};
        return new VectorImpl(v);
    }

    public Vector getPenaltyDot() {
        double[] v = {
                (a1 + s1 - b1 - t1)*(p2*n3 - p3*n2) +
                (a2 + s2 - b2 - t2)*(p3*n1 - p1*n3) +
                (a3 + s3 - b3 - t3)*(p1*n2 - p2*n1) +
                n1*(ad1 + w2*s3 - w3*s2 - bd1 - p2*t3 + p3*t2) +
                n2*(ad2 + w3*s1 - w1*s3 - bd2 - p3*t1 + p1*t3) +
                n3*(ad3 + w1*s2 - w2*s1 - bd3 - p1*t2 + p2*t1) };
        return new VectorImpl(v);
    }

    public Map<GeneralizedBody, Matrix> getJacobian() {
        if (jacMap != null) return jacMap;
        jacMap = new java.util.HashMap<GeneralizedBody, Matrix>();
        double[][] m1 = {{
            n1, n2, n3, s2*n3 - s3*n2, s3*n1 - s1*n3, s1*n2 - s2*n1
        }};
        double[][] m2 = {{
            -n1, -n2, -n3,
            n2*(a3 + s3 - b3) - n3*(a2 + s2 - b2),
            n3*(a1 + s1 - b1) - n1*(a3 + s3 - b3),
            n1*(a2 + s2 - b2) - n2*(a1 + s1 - b1)
        }};
        jacMap.put(sphereBody, new MatrixImpl(m1));
        jacMap.put(faceBody, new MatrixImpl(m2));
        return jacMap;
    }

    public Map<GeneralizedBody, Matrix> getJacobianDot() {
        if (jacDotMap != null) return jacDotMap;
        jacDotMap = new java.util.HashMap<GeneralizedBody, Matrix>();
        double t4 = w1*s2;
        double t5 = w2*s1;
        double t6 = t4-t5;
        double t8 = w3*s1;
        double t9 = w1*s3;
        double t10 = -t8+t9;
        double t14 = w2*s3;
        double t15 = w3*s2;
        double t16 = t14-t15;
        double t22 = a2+s2-b2-t2;
        double t25 = p1*n2-p2*n1;
        double t27 = a3+s3-b3-t3;
        double t30 = -p3*n1+p1*n3;
        double t32 = p3*t1;
        double t33 = p1*t3;
        double t34 = ad2+t8-t9-bd2-t32+t33;
        double t37 = p1*t2;
        double t38 = p2*t1;
        double t39 = ad3+t4-t5-bd3-t37+t38;
        double t42 = t37-t38;
        double t44 = -t32+t33;
        double t46 = a1+s1-b1-t1;
        double t51 = p2*n3-p3*n2;
        double t53 = p2*t3;
        double t54 = p3*t2;
        double t55 = ad1+t14-t15-bd1-t53+t54;
        double t62 = t53-t54;
        double[][] m1 = {{ 0, 0, 0, -n2*t6-n3*t10, n1*t6-n3*t16, n1*t10+n2*t16 }};
        double[][] m2 = {{ 0, 0, 0,
            -t22*t25-t27*t30-2.0*t34*n3+2.0*t39*n2+n2*t42+n3*t44,
            t46*t25-t27*t51+2.0*t55*n3-2.0*t39*n1-n1*t42+n3*t62,
            t46*t30+t22*t51-2.0*t55*n2+2.0*t34*n1-n1*t44-n2*t62
        }};
        jacDotMap.put(sphereBody, new MatrixImpl(m1));
        jacDotMap.put(faceBody, new MatrixImpl(m2));
        return jacDotMap;
    }

    public int getDimension() {
        return 1;
    }

    public List<SimulationObject> getObjects() {
        List<SimulationObject> result = new java.util.ArrayList<SimulationObject>();
        result.add(sphereBody); result.add(faceBody);
        return result;
    }
}

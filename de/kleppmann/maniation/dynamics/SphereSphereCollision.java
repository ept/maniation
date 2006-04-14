package de.kleppmann.maniation.dynamics;

import java.util.List;
import java.util.Map;

import de.kleppmann.maniation.dynamics.GeneralizedBody.State;
import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.MatrixImpl;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.maths.VectorImpl;

public class SphereSphereCollision implements InequalityConstraint {
    
    private Body body1, body2;
    private Vector3D centre1, centre2;
    private double radius1, radius2;
    private Body.State body1State, body2State;
    private double a1, a2, a3, b1, b2, b3, ad1, ad2, ad3, bd1, bd2, bd3, w1, w2, w3, p1, p2, p3,
        s1, s2, s3, t1, t2, t3, d1, d2, d3, dd1, dd2, dd3;
    private Map<GeneralizedBody, Matrix> jacMap, jacDotMap;
    
    // Sphere centres are given in local coordinates
    public SphereSphereCollision(Body body1, Vector3D centre1, double radius1, 
            Body body2, Vector3D centre2, double radius2) {
        this.body1 = body1; this.body2 = body2;
        this.centre1 = centre1; this.centre2 = centre2;
        this.radius1 = radius1; this.radius2 = radius2;
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
            body1State = (Body.State) states.get(body1);
            body2State = (Body.State) states.get(body2);
        } catch (ClassCastException e) {
            throw new IllegalStateException(e);
        }
        a1 = body1State.getCoMPosition().getComponent(0);
        a2 = body1State.getCoMPosition().getComponent(1);
        a3 = body1State.getCoMPosition().getComponent(2);
        b1 = body2State.getCoMPosition().getComponent(0);
        b2 = body2State.getCoMPosition().getComponent(1);
        b3 = body2State.getCoMPosition().getComponent(2);
        ad1 = body1State.getCoMVelocity().getComponent(0);
        ad2 = body1State.getCoMVelocity().getComponent(1);
        ad3 = body1State.getCoMVelocity().getComponent(2);
        bd1 = body2State.getCoMVelocity().getComponent(0);
        bd2 = body2State.getCoMVelocity().getComponent(1);
        bd3 = body2State.getCoMVelocity().getComponent(2);
        w1 = body1State.getAngularVelocity().getComponent(0);
        w2 = body1State.getAngularVelocity().getComponent(1);
        w3 = body1State.getAngularVelocity().getComponent(2);
        p1 = body2State.getAngularVelocity().getComponent(0);
        p2 = body2State.getAngularVelocity().getComponent(1);
        p3 = body2State.getAngularVelocity().getComponent(2);
        Vector3D s = body1State.getOrientation().transform(centre1);
        Vector3D t = body2State.getOrientation().transform(centre2);
        s1 = s.getComponent(0);
        s2 = s.getComponent(1);
        s3 = s.getComponent(2);
        t1 = t.getComponent(0);
        t2 = t.getComponent(1);
        t3 = t.getComponent(2);
        d1 = a1 + s1 - b1 - t1;
        d2 = a2 + s2 - b2 - t2;
        d3 = a3 + s3 - b3 - t3;
        dd1 = ad1 + w2*s3 - w3*s2 - bd1 - p2*t3 + p3*t2;
        dd2 = ad2 + w3*s1 - w1*s3 - bd2 - p3*t1 + p1*t3;
        dd3 = ad3 + w1*s2 - w2*s1 - bd3 - p1*t2 + p2*t1;
    }

    public Vector getPenalty() {
        double[] v = {d1*d1 + d2*d2 + d3*d3 - (radius1 + radius2)*(radius1 + radius2)};
        return new VectorImpl(v);
    }

    public Vector getPenaltyDot() {
        double[] v = {2*(d1*dd1 + d2*dd2 + d3*dd3)};
        return new VectorImpl(v);
    }

    public Map<GeneralizedBody, Matrix> getJacobian() {
        if (jacMap != null) return jacMap;
        jacMap = new java.util.HashMap<GeneralizedBody, Matrix>();
        double[][] m1 = {{
            2*d1, 2*d2, 2*d3, 2*(s2*d3-s3*d2), 2*(s3*d1-s1*d3), 2*(s1*d2-s2*d1)
        }};
        double[][] m2 = {{
            -2*d1, -2*d2, -2*d3, 2*(d2*t3-d3*t2), 2*(d3*t1-d1*t3), 2*(d1*t2-d2*t1)
        }};
        jacMap.put(body1, new MatrixImpl(m1));
        jacMap.put(body2, new MatrixImpl(m2));
        return jacMap;
    }

    public Map<GeneralizedBody, Matrix> getJacobianDot() {
        if (jacDotMap != null) return jacDotMap;
        jacDotMap = new java.util.HashMap<GeneralizedBody, Matrix>();
        double[][] m1 = {{
            2*dd1, 2*dd2, 2*dd3,
            2*d2*(w2*s1 - w1*s2) + 2*d3*(w3*s1 - w1*s3) + dd3*s2 - dd2*s3,
            2*d3*(w3*s2 - w2*s3) + 2*d1*(w1*s2 - w2*s1) + dd1*s3 - dd3*s1,
            2*d1*(w1*s3 - w3*s1) + 2*d2*(w2*s3 - w3*s2) + dd2*s1 - dd1*s2
        }};
        double[][] m2 = {{
            -2*dd1, -2*dd2, -2*dd3,
            2*d2*(t2*p1 - t1*p2) + 2*d3*(t3*p1 - t1*p3) - dd3*t2 + dd2*t3,
            2*d3*(t3*p2 - t2*p3) + 2*d1*(t1*p2 - t2*p1) - dd1*t3 + dd3*t1,
            2*d1*(t1*p3 - t3*p1) + 2*d2*(t2*p3 - t3*p2) - dd2*t1 + dd1*t2
        }};
        jacDotMap.put(body1, new MatrixImpl(m1));
        jacDotMap.put(body2, new MatrixImpl(m2));
        return jacDotMap;
    }

    public int getDimension() {
        return 1;
    }

    public List<SimulationObject> getObjects() {
        List<SimulationObject> result = new java.util.ArrayList<SimulationObject>();
        result.add(body1); result.add(body2);
        return result;
    }
}

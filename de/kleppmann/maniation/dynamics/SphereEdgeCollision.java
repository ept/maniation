package de.kleppmann.maniation.dynamics;

import java.util.List;
import java.util.Map;

import de.kleppmann.maniation.dynamics.GeneralizedBody.State;
import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.MatrixImpl;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.maths.VectorImpl;

public class SphereEdgeCollision implements InequalityConstraint {

    private final Body sphereBody, edgeBody;
    private final Vector3D centre, point, direction;
    private final double radius;
    private Body.State sphereBodyState, edgeBodyState;
    private double a1, a2, a3, b1, b2, b3, ad1, ad2, ad3, bd1, bd2, bd3, w1, w2, w3, p1, p2, p3,
        s1, s2, s3, t1, t2, t3, d1, d2, d3;
    private Map<GeneralizedBody, Matrix> jacMap, jacDotMap;
    
    // All vectors are given in local coordinates
    public SphereEdgeCollision(Body sphereBody, Vector3D centre, double radius, 
            Body edgeBody, Vector3D point, Vector3D direction) {
        this.sphereBody = sphereBody; this.edgeBody = edgeBody;
        this.centre = centre; this.point = point; this.direction = direction.normalize();
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
            edgeBodyState = (Body.State) states.get(edgeBody);
        } catch (ClassCastException e) {
            throw new IllegalStateException(e);
        }
        a1 = sphereBodyState.getCoMPosition().getComponent(0);
        a2 = sphereBodyState.getCoMPosition().getComponent(1);
        a3 = sphereBodyState.getCoMPosition().getComponent(2);
        b1 = edgeBodyState.getCoMPosition().getComponent(0);
        b2 = edgeBodyState.getCoMPosition().getComponent(1);
        b3 = edgeBodyState.getCoMPosition().getComponent(2);
        ad1 = sphereBodyState.getCoMVelocity().getComponent(0);
        ad2 = sphereBodyState.getCoMVelocity().getComponent(1);
        ad3 = sphereBodyState.getCoMVelocity().getComponent(2);
        bd1 = edgeBodyState.getCoMVelocity().getComponent(0);
        bd2 = edgeBodyState.getCoMVelocity().getComponent(1);
        bd3 = edgeBodyState.getCoMVelocity().getComponent(2);
        w1 = sphereBodyState.getAngularVelocity().getComponent(0);
        w2 = sphereBodyState.getAngularVelocity().getComponent(1);
        w3 = sphereBodyState.getAngularVelocity().getComponent(2);
        p1 = edgeBodyState.getAngularVelocity().getComponent(0);
        p2 = edgeBodyState.getAngularVelocity().getComponent(1);
        p3 = edgeBodyState.getAngularVelocity().getComponent(2);
        Vector3D s = sphereBodyState.getOrientation().transform(centre);
        Vector3D t = edgeBodyState.getOrientation().transform(point);
        Vector3D d = edgeBodyState.getOrientation().transform(direction);
        s1 = s.getComponent(0);
        s2 = s.getComponent(1);
        s3 = s.getComponent(2);
        t1 = t.getComponent(0);
        t2 = t.getComponent(1);
        t3 = t.getComponent(2);
        d1 = d.getComponent(0);
        d2 = d.getComponent(1);
        d3 = d.getComponent(2);
    }

    public Vector getPenalty() {
        double v1 = d2*(a3 + s3 - b3 - t3) - d3*(a2 + s2 - b2 - t2);
        double v2 = d3*(a1 + s1 - b1 - t1) - d1*(a3 + s3 - b3 - t3);
        double v3 = d1*(a2 + s2 - b2 - t2) - d2*(a1 + s1 - b1 - t1);
        double[] v = {v1*v1 + v2*v2 + v3*v3 - radius*radius};
        return new VectorImpl(v);
    }

    public Vector getPenaltyDot() {
        Matrix js = getJacobian().get(sphereBody);
        Matrix je = getJacobian().get(edgeBody);
        return js.mult(sphereBodyState.getVelocities()).add(je.mult(edgeBodyState.getVelocities()));
    }

    public Map<GeneralizedBody, Matrix> getJacobian() {
        if (jacMap != null) return jacMap;
        jacMap = new java.util.HashMap<GeneralizedBody, Matrix>();
        double t4 = a3+s3-b3-t3;
        double t6 = a1+s1-b1-t1;
        double t7 = t6*d3;
        double t8 = t4*d1-t7;
        double t11 = a2+s2-b2-t2;
        double t12 = t11*d1;
        double t13 = t6*d2-t12;
        double t15 = -t8*d3+t13*d2;
        double t17 = t4*d2;
        double t18 = t11*d3-t17;
        double t21 = t18*d3-t13*d1;
        double t24 = -t18*d2+t8*d1;
        double t25 = -t21;
        double t27 = -t24;
        double t30 = -t15;
        double t37 = d3*t3;
        double t38 = d2*t2;
        double t39 = -t4;
        double t45 = -t6;
        double t56 = d1*t1;
        double t62 = -t11;
        double[][] m1 = {{
            2.0*t15, 2.0*t21, 2.0*t24,
            -2.0*t25*s3+2.0*t27*s2,
            2.0*t30*s3-2.0*t27*s1,
            -2.0*t30*s2+2.0*t25*s1
        }};
        double[][] m2 = {{ -m1[0][0], -m1[0][1], -m1[0][2],
            -2.0*t18*(-t37-t38+t39*d3-t11*d2)-2.0*t8*(d1*t2-t45*d2)-2.0*t13*(d1*t3+t7),
            -2.0*t18*(d2*t1+t12)-2.0*t8*(-t37-t56-t4*d3+t45*d1)-2.0*t13*(d2*t3-t62*d3),
            -2.0*t18*(d3*t1-t39*d1)-2.0*t8*(d3*t2+t17)-2.0*t13*(-t38-t56+t62*d2-t6*d1)
        }};
        jacMap.put(sphereBody, new MatrixImpl(m1));
        jacMap.put(edgeBody, new MatrixImpl(m2));
        return jacMap;
    }

    public Map<GeneralizedBody, Matrix> getJacobianDot() {
        if (jacDotMap != null) return jacDotMap;
        jacDotMap = new java.util.HashMap<GeneralizedBody, Matrix>();
        double t4 = w1*s2;
        double t5 = w2*s1;
        double t6 = p1*t2;
        double t7 = p2*t1;
        double t8 = ad3+t4-t5-bd3-t6+t7;
        double t10 = w2*s3;
        double t11 = w3*s2;
        double t12 = p2*t3;
        double t13 = p3*t2;
        double t14 = ad1+t10-t11-bd1-t12+t13;
        double t16 = t8*d1-t14*d3;
        double t17 = t16*d3;
        double t19 = w3*s1;
        double t20 = w1*s3;
        double t21 = p3*t1;
        double t22 = p1*t3;
        double t23 = ad2+t19-t20-bd2-t21+t22;
        double t25 = t14*d2-t23*d1;
        double t26 = t25*d2;
        double t27 = a3+s3-b3-t3;
        double t30 = p2*d3-p3*d2;
        double t32 = a1+s1-b1-t1;
        double t35 = p1*d2-p2*d1;
        double t37 = t27*t30-t32*t35;
        double t38 = t37*d3;
        double t41 = p3*d1-p1*d3;
        double t43 = a2+s2-b2-t2;
        double t45 = t32*t41-t43*t30;
        double t46 = t45*d2;
        double t49 = t27*d1-t32*d3;
        double t50 = t49*t35;
        double t53 = t32*d2-t43*d1;
        double t54 = -t41;
        double t55 = t53*t54;
        double t59 = t23*d3-t8*d2;
        double t60 = t59*d3;
        double t61 = t25*d1;
        double t64 = t43*t35-t27*t41;
        double t65 = t64*d3;
        double t66 = t45*d1;
        double t69 = t43*d3-t27*d2;
        double t70 = -t35;
        double t71 = t69*t70;
        double t72 = t53*t30;
        double t74 = t59*d2;
        double t75 = t16*d1;
        double t76 = t64*d2;
        double t77 = t37*d1;
        double t78 = t69*t41;
        double t79 = -t30;
        double t80 = t49*t79;
        double t82 = -t60+t61;
        double t84 = t74-t75;
        double t86 = -t65+t66;
        double t88 = t76-t77;
        double t90 = t71+t72;
        double t92 = t78+t80;
        double t96 = -t69*d3+t53*d1;
        double t97 = t4-t5;
        double t101 = t69*d2-t49*d1;
        double t102 = -t19+t20;
        double t105 = t17-t26;
        double t108 = t38-t46;
        double t111 = t50+t55;
        double t116 = t49*d3-t53*d2;
        double t119 = t10-t11;
        double t139 = t6-t7;
        double t141 = -t21+t22;
        double t143 = -t27;
        double t146 = t59*t143+t25*t32;
        double t149 = -t32;
        double t151 = t59*t43+t16*t149;
        double t155 = t64*t143+t45*t32;
        double t159 = t64*t43+t37*t149;
        double t164 = -t69*t8+t53*t14;
        double t169 = t69*t23-t49*t14;
        double t173 = t69*t143+t53*t32;
        double t177 = t69*t43+t49*t149;
        double t179 = -t82*t3+t84*t2-t86*t3+t88*t2-t90*t3+t92*t2-t96*t139-t101*t141-t146*d3+t151*d2-t155*d3+t159*d2-t164*d3+t169*d2-t173*t35-t177*t54;
        double t188 = t12-t13;
        double t191 = -t43;
        double t193 = t16*t27+t25*t191;
        double t198 = t37*t27+t45*t191;
        double t204 = t49*t8-t53*t23;
        double t209 = t49*t27+t53*t191;
        double t212 = t105*t3-t84*t1+t108*t3-t88*t1+t111*t3-t92*t1+t116*t139-t101*t188+t193*d3-t151*d1+t198*d3-t159*d1+t204*d3-t169*d1-t209*t70-t177*t30;
        double t231 = -t105*t2+t82*t1-t108*t2+t86*t1-t111*t2+t90*t1+t116*t141+t96*t188-t193*d2+t146*d1-t198*d2+t155*d1-t204*d2+t164*d1-t209*t41-t173*t79;
        double[][] m1 = {{
            -2.0*t17+2.0*t26-2.0*t38+2.0*t46-2.0*t50-2.0*t55,
            2.0*t60-2.0*t61+2.0*t65-2.0*t66-2.0*t71-2.0*t72,
            -2.0*t74+2.0*t75-2.0*t76+2.0*t77-2.0*t78-2.0*t80,
            -2.0*t82*s3+2.0*t84*s2-2.0*t86*s3+2.0*t88*s2-2.0*t90*s3+2.0*t92*s2-2.0*t96*t97-2.0*t101*t102,
            2.0*t105*s3-2.0*t84*s1+2.0*t108*s3-2.0*t88*s1+2.0*t111*s3-2.0*t92*s1+2.0*t116*t97-2.0*t101*t119,
            -2.0*t105*s2+2.0*t82*s1-2.0*t108*s2+2.0*t86*s1-2.0*t111*s2+2.0*t90*s1+2.0*t116*t102+2.0*t96*t119
        }};
        double[][] m2 = {{ -m1[0][0], -m1[0][1], -m1[0][2], 2.0*t179, 2.0*t212, 2.0*t231 }};
        jacDotMap.put(sphereBody, new MatrixImpl(m1));
        jacDotMap.put(edgeBody, new MatrixImpl(m2));
        return jacDotMap;
    }

    public int getDimension() {
        return 1;
    }

    public List<SimulationObject> getObjects() {
        List<SimulationObject> result = new java.util.ArrayList<SimulationObject>();
        result.add(sphereBody); result.add(edgeBody);
        return result;
    }
}

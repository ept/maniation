package de.kleppmann.maniation.dynamics;

import java.util.List;
import java.util.Map;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.MatrixImpl;
import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.maths.VectorImpl;

public class RotationConstraint implements InequalityConstraint {
    
    private final World world;
    private final Body body1, body2;
    private Body.State body1State, body2State;
    private final Vector3D normal;
    private final double limit;
    private final Quaternion restDifference;
    private double n1, n2, n3, pw, px, py, pz, v1, v2, v3, qw, qx, qy, qz, w1, w2, w3;

    // normal is given in local coordinates of body1.
    // if body1State is null, normal is in world coordinates
    public RotationConstraint(World world, Body body1, Vector3D normal, Body body2, double limit) {
        this.world = world;
        this.body1 = body1;
        this.body2 = body2;
        this.normal = normal.normalize();
        this.limit = limit;
        if (body1 != null)
            this.restDifference = body1.getInitialState().getOrientation().getInverse().mult(
                    body2.getInitialState().getOrientation());
        else this.restDifference = new Quaternion();
    }
    
    public void setStateMapping(Map<GeneralizedBody, GeneralizedBody.State> states) {
        try {
            body1State = (Body.State) states.get(body1);
            body2State = (Body.State) states.get(body2);
        } catch (ClassCastException e) {
            throw new IllegalStateException(e);
        }
        Quaternion q = body2State.getOrientation();
        qw = q.getW(); qx = q.getX(); qy = q.getY(); qz = q.getZ();
        w1 = body2State.getAngularVelocity().getComponent(0);
        w2 = body2State.getAngularVelocity().getComponent(1);
        w3 = body2State.getAngularVelocity().getComponent(2);
        if (body1State != null) {
            Quaternion p = body1State.getOrientation().mult(restDifference);
            pw = p.getW(); px = p.getX(); py = p.getY(); pz = p.getZ();
            v1 = body1State.getAngularVelocity().getComponent(0);
            v2 = body1State.getAngularVelocity().getComponent(1);
            v3 = body1State.getAngularVelocity().getComponent(2);
        } else {
            pw = 1.0;
            px = py = pz = v1 = v2 = v3 = 0.0;
        }
        n1 = normal.getComponent(0);
        n2 = normal.getComponent(1);
        n3 = normal.getComponent(2);
        if ((pw*qw + px*qx + py*qy + pz*qz < 0) ^ (limit > 0)) {
            n1 *= -1; n2 *= -1; n3 *= -1;
        }
    }

    public List<SimulationObject> getObjects() {
        List<SimulationObject> result = new java.util.ArrayList<SimulationObject>();
        if (body1 != null) result.add(body1); else result.add(world);
        result.add(body2);
        return result;
    }

    public int getDimension() {
        return 1;
    }

    public boolean isInequality() {
        return (limit != 0.0);
    }

    public Map<Body, Vector3D> setToZero() {
        return new java.util.HashMap<Body, Vector3D>();
    }

    public Vector getPenalty() {
        double[] arr = new double[1];
        arr[0] = n1*(py*qz - pz*qy - pw*qx + px*qw) +
                 n2*(pz*qx - px*qz - pw*qy + py*qw) +
                 n3*(px*qy - py*qx - pw*qz + pz*qw);
        arr[0] = Math.abs(limit) + arr[0];
        return new VectorImpl(arr);
    }

    public Vector getPenaltyDot() {
        double x1 = 0.5*(n1*(px*qx + pw*qw - pz*qz - py*qy) +
                         n2*(py*qx - pz*qw - pw*qz + px*qy) +
                         n3*(pz*qx + py*qw + px*qz + pw*qy));
        double x2 = 0.5*(n1*(px*qy + pw*qz + pz*qw + py*qx) +
                         n2*(py*qy - pz*qz + pw*qw - px*qx) +
                         n3*(pz*qy + py*qz - px*qw - pw*qx));
        double x3 = 0.5*(n1*(px*qz - pw*qy + pz*qx - py*qw) +
                         n2*(py*qz + pz*qy + pw*qx + px*qw) +
                         n3*(pz*qz - py*qy - px*qx + pw*qw));
        double[] arr = new double[1];
        arr[0] = x1*v1 + x2*v2 + x3*v3 - x1*w1 - x2*w2 - x3*w3;
        return new VectorImpl(arr);
    }

    public Map<GeneralizedBody, Matrix> getJacobian() {
        double[][] m1 = {{0, 0, 0,
            0.5*(n1*(px*qx + pw*qw - pz*qz - py*qy) +
                 n2*(py*qx - pz*qw - pw*qz + px*qy) +
                 n3*(pz*qx + py*qw + px*qz + pw*qy)),
            0.5*(n1*(px*qy + pw*qz + pz*qw + py*qx) +
                 n2*(py*qy - pz*qz + pw*qw - px*qx) +
                 n3*(pz*qy + py*qz - px*qw - pw*qx)),
            0.5*(n1*(px*qz - pw*qy + pz*qx - py*qw) +
                 n2*(py*qz + pz*qy + pw*qx + px*qw) +
                 n3*(pz*qz - py*qy - px*qx + pw*qw)) }};
        Map<GeneralizedBody, Matrix> result = new java.util.HashMap<GeneralizedBody, Matrix>();
        if (body1State != null) result.put(body1, new MatrixImpl(m1));
        double[][] m2 = {{0, 0, 0, -m1[0][3], -m1[0][4], -m1[0][5]}};
        result.put(body2, new MatrixImpl(m2));
        return result;
    }

    public Map<GeneralizedBody, Matrix> getJacobianDot() {
        double x1 = n1*w1 + n2*w2 + n3*w3;
        double x2 = py*w3 - pz*w2;
        double x3 = pz*w1 - px*w3;
        double x4 = px*w2 - py*w1;
        double x5 = py*v3 - pz*v2;
        double x6 = pz*v1 - px*v3;
        double x7 = px*v2 - py*v1;
        double x8 = n1*(qy*w3 - qz*w2) + n2*(qz*w1 - qx*w3) + n3*(qx*w2 - qy*w1);
        double x9 = n1*x2 + n2*x3 + n3*x4;
        double x10 = (pw*w2 - x3)*n3 - (pw*w3 - x4)*n2;
        double x11 = (pw*w3 - x4)*n1 - (pw*w1 - x2)*n3;
        double x12 = (pw*w1 - x2)*n2 - (pw*w2 - x3)*n1;
        double x13 = (pw*v2 - x6)*n3 - (pw*v3 - x7)*n2;
        double x14 = (pw*v3 - x7)*n1 - (pw*v1 - x5)*n3;
        double x15 = (pw*v1 - x5)*n2 - (pw*v2 - x6)*n1;
        double x16 = n1*v1 + n2*v2 + n3*v3;
        double x17 = n1*x5 + n2*x6 + n3*x7;
        double x18 = n1*(qy*v3 - qz*v2) + n2*(qz*v1 - qx*v3) + n3*(qx*v2 - qy*v1);
        Map<GeneralizedBody, Matrix> result = new java.util.HashMap<GeneralizedBody, Matrix>();
        if (body1State != null) {
            double[][] m1 = {{0, 0, 0,
                0.5 *(qw*x1*px - x8*px) +
                0.25*(pw*x16*qx - x17*qx + x13*qw - x14*qz + x15*qy - qw*x16*px + x18*px),
                0.5 *(qw*x1*py - x8*py) +
                0.25*(pw*x16*qy - x17*qy + x13*qz + x14*qw - x15*qx - qw*x16*py + x18*py),
                0.5 *(qw*x1*pz - x8*pz) +
                0.25*(pw*x16*qz - x17*qz - x13*qy + x14*qx + x15*qw - qw*x16*pz + x18*pz) }};
            result.put(body1, new MatrixImpl(m1));
        }
        double[][] m2 = {{0, 0, 0,
            0.25*(pw*x1*qx - qw*x1*px - x9*qx + x10*qw - x11*qz + x12*qy + x8*px) +
            0.5 *(x17*qx - x13*qw + x14*qz - x15*qy - x16*pw*qx),
            0.25*(pw*x1*qy - qw*x1*py - x9*qy + x10*qz + x11*qw - x12*qx + x8*py) +
            0.5 *(x17*qy - x13*qz - x14*qw + x15*qx - x16*pw*qy),
            0.25*(pw*x1*qz - qw*x1*pz - x9*qz - x10*qy + x11*qx + x12*qw + x8*pz) +
            0.5 *(x17*qz + x13*qy - x14*qx - x15*qw - x16*pw*qz) }};
        result.put(body2, new MatrixImpl(m2));
        return result;
    }
}

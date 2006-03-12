package de.kleppmann.maniation.dynamics;

import java.util.List;
import java.util.Map;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.MatrixImpl;
import de.kleppmann.maniation.maths.Vector3D;

public class JointConstraint implements Constraint {
    
    private Body.State body1State, body2State;
    private Vector3D localPos1, localPos2;

    // All vectors are in local coordinates of the respective body.
    public JointConstraint(Body.State body1State, Vector3D localPos1,
            Body.State body2State, Vector3D localPos2) {
        this.body1State = body1State; this.localPos1 = localPos1;
        this.body2State = body2State; this.localPos2 = localPos2;
    }

    public List<SimulationObject> getObjects() {
        List<SimulationObject> result = new java.util.ArrayList<SimulationObject>();
        result.add(body1State.getOwner());
        result.add(body2State.getOwner());
        return result;
    }

    public int getDimension() {
        return 3;
    }

    public Vector3D getPenalty() {
        Vector3D s = body1State.getOrientation().transform(localPos1);
        Vector3D t = body2State.getOrientation().transform(localPos2);
        return body1State.getCoMPosition().add(s).subtract(body2State.getCoMPosition()).subtract(t);
    }

    public Vector3D getPenaltyDot() {
        Vector3D s = body1State.getOrientation().transform(localPos1);
        Vector3D t = body2State.getOrientation().transform(localPos2);
        return body1State.getCoMVelocity().add(body1State.getAngularVelocity().cross(s)).
            subtract(body2State.getCoMVelocity()).subtract(body2State.getAngularVelocity().cross(t));
    }

    public Map<GeneralizedBody, Matrix> getJacobian() {
        Vector3D s = body1State.getOrientation().transform(localPos1);
        Vector3D t = body2State.getOrientation().transform(localPos2);
        double s1 = s.getComponent(0), s2 = s.getComponent(1), s3 = s.getComponent(2);
        double t1 = t.getComponent(0), t2 = t.getComponent(1), t3 = t.getComponent(2);
        double[][] j1 = { 
                {1,   0,   0,   0,   s3, -s2},
                {0,   1,   0,  -s3,  0,   s1},
                {0,   0,   1,   s2, -s1,  0 }};
        double[][] j2 = { 
                {-1,  0,   0,   0,  -t3,  t2},
                {0,   -1,  0,   t3,  0,  -t1},
                {0,   0,   -1, -t2,  t1,  0 }};
        Map<GeneralizedBody, Matrix> result = new java.util.HashMap<GeneralizedBody, Matrix>();
        result.put(body1State.getOwner(), new MatrixImpl(j1));
        result.put(body2State.getOwner(), new MatrixImpl(j2));
        return result;
    }

    public Map<GeneralizedBody, Matrix> getJacobianDot() {
        Vector3D s = body1State.getOrientation().transform(localPos1);
        Vector3D t = body2State.getOrientation().transform(localPos2);
        double s1 = s.getComponent(0), s2 = s.getComponent(1), s3 = s.getComponent(2);
        double t1 = t.getComponent(0), t2 = t.getComponent(1), t3 = t.getComponent(2);
        double v1 = body1State.getAngularVelocity().getComponent(0);
        double v2 = body1State.getAngularVelocity().getComponent(1);
        double v3 = body1State.getAngularVelocity().getComponent(2);
        double w1 = body2State.getAngularVelocity().getComponent(0);
        double w2 = body2State.getAngularVelocity().getComponent(1);
        double w3 = body2State.getAngularVelocity().getComponent(2);
        double[][] jdot1 = {
                {0, 0, 0,   0,              v1*s2-v2*s1,    v1*s3-v3*s1},
                {0, 0, 0,   v2*s1-v1*s2,    0,              v2*s3-v3*s2},
                {0, 0, 0,   v3*s1-v1*s3,    v3*s2-v2*s3,    0          }};
        double[][] jdot2 = {
                {0, 0, 0,   0,              w2*t1-w1*t2,    w3*t1-w1*t3},
                {0, 0, 0,   w1*t2-w2*t1,    0,              w3*t2-w2*t3},
                {0, 0, 0,   w1*t3-w3*t1,    w2*t3-w3*t2,    0          }};
        Map<GeneralizedBody, Matrix> result = new java.util.HashMap<GeneralizedBody, Matrix>();
        result.put(body1State.getOwner(), new MatrixImpl(jdot1));
        result.put(body2State.getOwner(), new MatrixImpl(jdot2));
        return result;
    }
}

package de.kleppmann.maniation.dynamics;

import java.util.List;
import java.util.Map;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.MatrixImpl;
import de.kleppmann.maniation.maths.Vector3D;

public class NailConstraint implements Constraint {

    private World world;
    private RigidBody body;
    private Vector3D localPoint, target;

    public NailConstraint(World world, RigidBody body, Vector3D localPoint, Vector3D target) {
        this.world = world; this.body = body; this.localPoint = localPoint; this.target = target;
    }

    public List<SimulationObject> getObjects() {
        List<SimulationObject> result = new java.util.ArrayList<SimulationObject>();
        result.add(world);
        result.add(body);
        return result;
    }

    public Vector3D getPenalty() {
        Vector3D s = body.getOrientation().transform(localPoint);
        return body.getCoMPosition().add(s).subtract(target);
    }

    public Vector3D getPenaltyDot() {
        Vector3D s = body.getOrientation().transform(localPoint);
        return body.getCoMVelocity().add(body.getAngularVelocity().cross(s));
    }

    public Map<RigidBody, Matrix> getJacobian() {
        Vector3D s = body.getOrientation().transform(localPoint);
        double s1 = s.getComponent(0), s2 = s.getComponent(1), s3 = s.getComponent(2);
        double[][] j = {
                {1,   0,   0,   0,   s3, -s2},
                {0,   1,   0,  -s3,  0,   s1},
                {0,   0,   1,   s2, -s1,  0}};     
        Matrix mat = new MatrixImpl(j);
        Map<RigidBody, Matrix> result = new java.util.HashMap<RigidBody, Matrix>();
        result.put(body, mat);
        return result;
    }

    public Map<RigidBody, Matrix> getJacobianDot() {
        Vector3D s = body.getOrientation().transform(localPoint);
        double s1 = s.getComponent(0), s2 = s.getComponent(1), s3 = s.getComponent(2);
        double w1 = body.getAngularVelocity().getComponent(0);
        double w2 = body.getAngularVelocity().getComponent(1);
        double w3 = body.getAngularVelocity().getComponent(2);
        double[][] jdot = {
                {0, 0, 0,   0,              w1*s2-w2*s1,    w1*s3-w3*s1},
                {0, 0, 0,   w2*s1-w1*s2,    0,              w2*s3-w3*s2},
                {0, 0, 0,   w3*s1-w1*s3,    w3*s2-w2*s3,    0          }};
        Matrix mat = new MatrixImpl(jdot);
        Map<RigidBody, Matrix> result = new java.util.HashMap<RigidBody, Matrix>();
        result.put(body, mat);
        return result;
    }
}

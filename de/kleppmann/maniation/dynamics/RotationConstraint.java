package de.kleppmann.maniation.dynamics;

import java.util.Map;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.MatrixImpl;
import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.maths.VectorImpl;

public class RotationConstraint implements Constraint {
    
    private RigidBody body1, body2;
    private Vector3D normal;

    public RotationConstraint(RigidBody body1, RigidBody body2, Vector3D normal) {
        this.body1 = body1; this.body2 = body2; this.normal = normal;
    }

    public Vector getPenalty() {
        Quaternion q = body2.getOrientation().getInverse().mult(body1.getOrientation());
        double[] arr = new double[1];
        arr[0] = -normal.getComponent(0)*q.getX() - normal.getComponent(1)*q.getY() -
            normal.getComponent(2)*q.getZ();
        return new VectorImpl(arr);
    }

    public Vector getPenaltyDot() {
        Quaternion q = body2.getOrientation().getInverse().mult(body1.getOrientation());
        double n1 = normal.getComponent(0), n2 = normal.getComponent(1),
            n3 = normal.getComponent(2);
        double qw = q.getW(), qx = q.getX(), qy = q.getY(), qz = q.getZ();
        double w1 = body1.getAngularVelocity().getComponent(0);
        double w2 = body1.getAngularVelocity().getComponent(1);
        double w3 = body1.getAngularVelocity().getComponent(2);
        double p1 = body2.getAngularVelocity().getComponent(0);
        double p2 = body2.getAngularVelocity().getComponent(1);
        double p3 = body2.getAngularVelocity().getComponent(2);
        double[] arr = new double[1];
        arr[0] = 0.5*(
            (n2*qz - n3*qy - n1*qw) * w1  +
            (n3*qx - n1*qz - n2*qw) * w2  +
            (n1*qy - n2*qx - n3*qw) * w3) +
            (n3*qy - n2*qz) * p1 +
            (n1*qz - n3*qx) * p2 +
            (n2*qx - n1*qy) * p3;
        return new VectorImpl(arr);
    }

    public Map<RigidBody, Matrix> getJacobian() {
        Quaternion q = body2.getOrientation().getInverse().mult(body1.getOrientation());
        double n1 = normal.getComponent(0), n2 = normal.getComponent(1),
            n3 = normal.getComponent(2);
        double qw = q.getW(), qx = q.getX(), qy = q.getY(), qz = q.getZ();
        double[][] m1 = {{0, 0, 0,
            n2*qz - n3*qy - n1*qw,
            n3*qx - n1*qz - n2*qw,
            n1*qy - n2*qx - n3*qw}};
        double[][] m2 = {{0, 0, 0,
            n3*qy - n2*qz,
            n1*qz - n3*qx,
            n2*qx - n1*qy}};
        Map<RigidBody, Matrix> result = new java.util.HashMap<RigidBody, Matrix>();
        result.put(body1, new MatrixImpl(m1));
        result.put(body2, new MatrixImpl(m2));
        return result;
    }

    public Map<RigidBody, Matrix> getJacobianDot() {
        Quaternion q = body2.getOrientation().getInverse().mult(body1.getOrientation());
        double n1 = normal.getComponent(0), n2 = normal.getComponent(1),
            n3 = normal.getComponent(2);
        double qw = q.getW(), qx = q.getX(), qy = q.getY(), qz = q.getZ();
        double w1 = body1.getAngularVelocity().getComponent(0);
        double w2 = body1.getAngularVelocity().getComponent(1);
        double w3 = body1.getAngularVelocity().getComponent(2);
        double p1 = body2.getAngularVelocity().getComponent(0);
        double p2 = body2.getAngularVelocity().getComponent(1);
        double p3 = body2.getAngularVelocity().getComponent(2);
        double[][] m1 = {{0, 0, 0,
            1.5 *qw*(p3*n2 - p2*n3) +
            1.5 *qy*(p2*n1 - p1*n2) +
            1.5 *qz*(p3*n1 - p1*n3) +
            0.25*w1*(qx*n1 + qy*n2  + qz*n3),
            1.5 *qw*(p1*n3 - p3*n1) +
            1.5 *qx*(p1*n2 - p2*n1) +
            1.5 *qz*(p3*n2 - p2*n3) +
            0.25*w2*(qx*n1 + qy*n2  + qz*n3),
            1.5 *qw*(p2*n1 - p1*n2) +
            1.5 *qy*(p2*n3 - p3*n2) +
            1.5 *qx*(p1*n3 - p3*n1) +
            0.25*w3*(qx*n1 + qy*n2  + qz*n3) }};
        double[][] m2 = {{0, 0, 0,
            qy*(p1*n2 - p2*n1) + qz*(p1*n3 - p3*n1),
            qx*(p2*n1 - p1*n2) + qz*(p2*n3 - p3*n2),
            qx*(p3*n1 - p1*n3) + qy*(p3*n2 - p2*n3) }};
        Map<RigidBody, Matrix> result = new java.util.HashMap<RigidBody, Matrix>();
        result.put(body1, new MatrixImpl(m1));
        result.put(body2, new MatrixImpl(m2));
        return result;
    }

}

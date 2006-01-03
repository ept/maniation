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
    private double n1, n2, n3, qw, qx, qy, qz, w1, w2, w3, p1, p2, p3;

    // normal is given in local coordinates of body1.
    // if body1 is null, normal is in world coordinates
    public RotationConstraint(RigidBody body1, Vector3D normal, RigidBody body2) {
        this.body1 = body1; this.body2 = body2; this.normal = normal;
    }
    
    private void updateNumbers() {
        Quaternion q = body2.getOrientation();
        qw = q.getW(); qx = q.getX(); qy = q.getY(); qz = q.getZ();
        w1 = body2.getAngularVelocity().getComponent(0);
        w2 = body2.getAngularVelocity().getComponent(1);
        w3 = body2.getAngularVelocity().getComponent(2);
        Vector3D n;
        if (body1 != null) {
            n = body1.getOrientation().transform(normal);
            p1 = body1.getAngularVelocity().getComponent(0);
            p2 = body1.getAngularVelocity().getComponent(1);
            p3 = body1.getAngularVelocity().getComponent(2);
        } else {
            n = normal;
            p1 = p2 = p3 = 0.0;
        }
        n1 = n.getComponent(0);
        n2 = n.getComponent(1);
        n3 = n.getComponent(2);
    }

    public Vector getPenalty() {
        updateNumbers();
        double[] arr = new double[1];
        arr[0] = - n1*qx - n2*qy - n3*qz;
        return new VectorImpl(arr);
    }

    public Vector getPenaltyDot() {
        updateNumbers();
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
        updateNumbers();
        Map<RigidBody, Matrix> result = new java.util.HashMap<RigidBody, Matrix>();
        if (body1 != null) {
            double[][] m1 = {{0, 0, 0,
                n3*qy - n2*qz,
                n1*qz - n3*qx,
                n2*qx - n1*qy}};
            result.put(body1, new MatrixImpl(m1));
        }
        double[][] m2 = {{0, 0, 0,
            0.5*(n2*qz - n3*qy - n1*qw),
            0.5*(n3*qx - n1*qz - n2*qw),
            0.5*(n1*qy - n2*qx - n3*qw)}};
        result.put(body2, new MatrixImpl(m2));
        return result;
    }

    public Map<RigidBody, Matrix> getJacobianDot() {
        updateNumbers();
        Map<RigidBody, Matrix> result = new java.util.HashMap<RigidBody, Matrix>();
        if (body1 != null) {
            double[][] m1 = {{0, 0, 0,
                qy*(p1*n2 - p2*n1) + qz*(p1*n3 - p3*n1),
                qx*(p2*n1 - p1*n2) + qz*(p2*n3 - p3*n2),
                qx*(p3*n1 - p1*n3) + qy*(p3*n2 - p2*n3) }};
            result.put(body1, new MatrixImpl(m1));
        }
        double[][] m2 = {{0, 0, 0,
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
        result.put(body2, new MatrixImpl(m2));
        return result;
    }
}

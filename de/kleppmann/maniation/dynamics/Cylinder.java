package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.Matrix33;
import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;

public class Cylinder extends RigidBody {
    
    private final Quaternion toPrincipalAxes;
    private final double mass, radial, axial;
    
    /**
     * Rigid body implementation of a cylinder with an arbitrary axis of symmetry.
     * The centre of mass is at the origin. Collisions are not handled.
     * @param axis Direction of the axis of symmetry (along the length of the cylinder).
     * @param radius Radius of the cylinder.
     * @param length Length of the cylinder.
     * @param mass Mass of the cylinder.
     */
    public Cylinder(Vector3D axis, double radius, double length, double mass) {
        this.mass = mass;
        toPrincipalAxes = Quaternion.fromDirectionRoll(axis, new Vector3D(0,0,1), 0.0);
        radial = (length*length + 3.0*radius*radius)*mass/12.0;
        axial = 0.5*mass*radius*radius;
    }
    
    protected double getMass() {
        return mass;
    }
    
    protected Matrix33 getInertia(Body.State state) {
        Matrix33 i = new Matrix33(new Vector3D(radial, radial, axial));
        Matrix33 rot = toPrincipalAxes.mult(state.getOrientation().getInverse()).toMatrix();
        return rot.transpose().mult33(i).mult33(rot);
    }

    protected Matrix33 getInvInertia(Body.State state) {
        Matrix33 i = new Matrix33(new Vector3D(1.0/radial, 1.0/radial, 1.0/axial));
        Matrix33 rot = toPrincipalAxes.mult(state.getOrientation().getInverse()).toMatrix();
        return rot.transpose().mult33(i).mult33(rot);
    }

    protected Vector3D getInitialPosition() {
        return new Vector3D();
    }

    protected Quaternion getInitialOrientation() {
        return new Quaternion();
    }

    protected Vector3D getInitialLinearMomentum() {
        return new Vector3D();
    }

    protected Vector3D getInitialAngularMomentum() {
        return new Vector3D();
    }
}

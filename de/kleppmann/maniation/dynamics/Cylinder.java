package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.Matrix33;
import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;

public class Cylinder extends RigidBody {
    
    // constant quantities
    private Quaternion toPrincipalAxes;
    private double mass;
    private Vector3D principalInertia;
    // derived quantities
    private Matrix33 inertia, invInertia;
    private boolean upToDate = false;
    
    /**
     * Rigid body implementation of a cylinder whose axis (when not rotated) is
     * parallel to the Z axis, and centre of mass at the origin. Collisions are
     * not handled.
     * @param radius Radius of the cylinder.
     * @param length Length of the cylinder.
     * @param mass Mass of the cylinder.
     */
    public Cylinder(double radius, double length, double mass) {
        this.mass = mass;
        toPrincipalAxes = new Quaternion();
        double inert = (length*length + 3.0*radius*radius)*mass/12.0;
        principalInertia = new Vector3D(inert, inert, 0.5*mass*radius*radius);
    }
    
    private void updateInertia() {
        if (upToDate) return;
        inertia = new Matrix33(principalInertia);
        invInertia = inertia.inverse();
        Matrix33 rot = toPrincipalAxes.mult(getOrientation().getInverse()).toMatrix();
        inertia    = rot.transpose().mult33(inertia   ).mult33(rot);
        invInertia = rot.transpose().mult33(invInertia).mult33(rot);
        upToDate = true;
    }

    protected double getMass() {
        return mass;
    }

    protected Matrix33 getInertia() {
        updateInertia();
        return inertia;
    }

    protected Matrix33 getInvInertia() {
        updateInertia();
        return invInertia;
    }

    protected void setOrientation(Quaternion orient) {
        upToDate = false;
        super.setOrientation(orient);
    }
}

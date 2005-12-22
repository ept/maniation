package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;

public class RigidBody {

    // Primary quantities
    private Vector3D pos, mom, angmom;
    private Quaternion orient, toPrincipalAxes; // orient transforms local to world
    private double mass;
    private Vector3D inertia, invInertia;
    private boolean upToDate = false;
    // Derived quantities
    private Vector3D vel, angvel;
    private Quaternion orientDot;
    private Vector3D forces, torques;

    public RigidBody() {
        pos = new Vector3D();
        mom = new Vector3D();
        angmom = new Vector3D();
        orient = new Quaternion();
        toPrincipalAxes = new Quaternion();
        mass = 1.0;
        inertia = new Vector3D(1.0, 1.0, 1.0);
        invInertia = new Vector3D(1.0, 1.0, 1.0);
    }
    
    private void deriveQuantities() {
        vel = mom.mult(1.0/mass);
        Quaternion r = toPrincipalAxes.mult(orient.getInverse());
        angvel = r.getInverse().transform(invInertia.multComponents(r.transform(angmom)));
        orientDot = (new Quaternion(angvel.mult(0.5))).mult(orient);
        forces = new Vector3D();
        torques = angvel.cross(angmom).mult(-1.0);
        upToDate = true;
    }

    public Vector3D getCoMPosition() {
        return pos;
    }
    
    protected void setCoMPosition(Vector3D pos) {
        this.pos = pos;
        upToDate = false;
    }
    
    public Vector3D getCoMVelocity() {
        if (!upToDate) deriveQuantities();
        return vel;
    }
    
    public Vector3D getLinearMomentum() {
        return mom;
    }
    
    protected void setLinearMomentum(Vector3D mom) {
        this.mom = mom;
        upToDate = false;
    }
    
    public Vector3D getForces() {
        if (!upToDate) deriveQuantities();
        return forces;
    }
    
    public Quaternion getOrientation() {
        return orient;
    }
    
    protected void setOrientation(Quaternion orient) {
        this.orient = orient;
        upToDate = false;
    }
    
    public Quaternion getRateOfRotation() {
        if (!upToDate) deriveQuantities();
        return orientDot;
    }
    
    public Vector3D getAngularVelocity() {
        if (!upToDate) deriveQuantities();
        return angvel;
    }
    
    public Vector3D getAngularMomentum() {
        return angmom;
    }
    
    protected void setAngularMomentum(Vector3D angmom) {
        this.angmom = angmom;
        upToDate = false;
    }
    
    public Vector3D getTorques() {
        if (!upToDate) deriveQuantities();
        return torques;
    }
}

package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.Matrix33;
import de.kleppmann.maniation.maths.MatrixImpl;
import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;

public class RigidBody {

    // Primary quantities
    private Vector3D pos, mom, angmom;
    private Quaternion orient, toPrincipalAxes; // orient transforms local to world
    private double mass;
    private Vector3D principalInertia;
    private boolean upToDate = false;
    // Derived quantities
    private Vector3D vel, angvel;
    private Quaternion orientDot;
    private Vector3D forces, torques;
    private Matrix33 inertia, invInertia;
    // Unchanging variables
    private MassInertia massInertia = new MassInertia();
    private MassInertia invMassInertia = new InverseMassInertia();

    public RigidBody() {
        pos = new Vector3D();
        mom = new Vector3D();
        angmom = new Vector3D();
        orient = new Quaternion();
        toPrincipalAxes = new Quaternion();
        mass = 1.0;
        principalInertia = new Vector3D(1.0, 1.0, 1.0);
    }
    
    private void deriveQuantities() {
        vel = mom.mult(1.0/mass);
        inertia = new Matrix33(principalInertia);
        invInertia = inertia.inverse();
        Matrix33 rot = toPrincipalAxes.mult(orient.getInverse()).toMatrix();
        inertia    = rot.transpose().mult33(inertia   ).mult33(rot);
        invInertia = rot.transpose().mult33(invInertia).mult33(rot);
        angvel = invInertia.mult(angmom);
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
    
    public MassInertia getMassInertia() {
        return massInertia;
    }
    
    public double getEnergy() {
        Vector3D gravity = new Vector3D(0, 1, 0);
        double potential = gravity.mult(getCoMPosition());
        double kinetic = 0.5*mass*getCoMVelocity().mult(getCoMVelocity());
        Vector3D omega = toPrincipalAxes.transform(
                getOrientation().getInverse().transform(getAngularVelocity()));
        Vector3D osq = omega.multComponents(omega);
        double rotational = 0.5*principalInertia.mult(osq);
        return potential + kinetic + rotational;
    }
    
    
    public class MassInertia extends MatrixImpl {
        private MassInertia() {
            super(null);
        }
        public int getRows() {
            return 6;
        }
        public int getColumns() {
            return 6;
        }

        public double getComponent(int row, int column) {
            if ((row < 3) || (column < 3)) {
                if (row == column) return mass; else return 0.0;
            }
            if (!upToDate) deriveQuantities();
            return inertia.getComponent(row - 3, column - 3);
        }

        public Matrix inverse() {
            return invMassInertia;
        }        
    }
    
    
    private class InverseMassInertia extends MassInertia {
        public double getComponent(int row, int column) {
            if ((row < 3) || (column < 3)) {
                if (row == column) return 1.0/mass; else return 0.0;
            }
            if (!upToDate) deriveQuantities();
            return invInertia.getComponent(row - 3, column - 3);
        }

        public Matrix inverse() {
            return massInertia;
        }
    }
}

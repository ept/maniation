package de.kleppmann.maniation;

import de.kleppmann.maniation.dynamics.ConstrainedRigidBodies;
import de.kleppmann.maniation.dynamics.JointConstraint;
import de.kleppmann.maniation.dynamics.NailConstraint;
import de.kleppmann.maniation.dynamics.RigidBody;
import de.kleppmann.maniation.dynamics.RotationConstraint;
import de.kleppmann.maniation.maths.Vector3D;

public class Centrifuge extends RigidBody {

    private boolean sphere;
    
    public Centrifuge(boolean sphere) {
        super();
        this.sphere = sphere;
        if (sphere) setCoMPosition(new Vector3D(0, 0.7, 2.2));
        else setCoMPosition(new Vector3D(0, 0, 0));
    }

    public Vector3D getForces() {
        return super.getForces().add(new Vector3D(0, 0, -1));
    }
    
    public Vector3D getTorques() {
        Vector3D result = super.getTorques();
        if (sphere) return result;
        return result.add(new Vector3D(0, 0, 0.01));
    }

    public static void setup(ConstrainedRigidBodies system) {
        Centrifuge cylinder = new Centrifuge(false);
        Centrifuge sphere = new Centrifuge(true);
        system.addBody(cylinder); system.addBody(sphere);
        system.addConstraint(new NailConstraint(cylinder,
                new Vector3D(0, 0, 0), new Vector3D(0, 0, 0)));
        system.addConstraint(new RotationConstraint(null, new Vector3D(1,0,0), cylinder));
        system.addConstraint(new RotationConstraint(null, new Vector3D(0,1,0), cylinder));
        system.addConstraint(new JointConstraint(cylinder, new Vector3D(0, 0.7, 3),
                    sphere, new Vector3D(0, 0, 0.8)));
        system.addConstraint(new RotationConstraint(cylinder, new Vector3D(0,1,0), sphere));
        system.addConstraint(new RotationConstraint(cylinder, new Vector3D(0,0,1), sphere));
    }
}

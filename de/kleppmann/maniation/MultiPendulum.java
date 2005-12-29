package de.kleppmann.maniation;

import de.kleppmann.maniation.dynamics.ConstrainedRigidBodies;
import de.kleppmann.maniation.dynamics.JointConstraint;
import de.kleppmann.maniation.dynamics.NailConstraint;
import de.kleppmann.maniation.dynamics.RigidBody;
import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;

public class MultiPendulum extends RigidBody {

    public MultiPendulum(int segment) {
        super();
        if (segment == 0) {
            setCoMPosition(new Vector3D(1.0/Math.sqrt(2.0), 1.0 - 1.0/Math.sqrt(2.0), 0.0));
            setOrientation(Quaternion.getZRotation(Math.PI/4.0));
        } else {
            setCoMPosition(new Vector3D(Math.sqrt(2.0), 2.0 - Math.sqrt(2.0) - 2.0*segment, 0.0));
        }
    }

    public Vector3D getForces() {
        return super.getForces().add(new Vector3D(0.0, -1.0, 0.0));
    }

    public static void setup(ConstrainedRigidBodies system, int segments) {
        RigidBody[] bodies = new RigidBody[segments];
        for (int i=0; i<segments; i++) {
            bodies[i] = new MultiPendulum(i);
            system.addBody(bodies[i]);
        }
        system.addConstraint(new NailConstraint(bodies[0],
                new Vector3D(0, 1, 0), new Vector3D(0, 1, 0)));
        for (int i=1; i<segments; i++)
            system.addConstraint(new JointConstraint(bodies[i-1], new Vector3D(0, -1, 0),
                    bodies[i], new Vector3D(0, 1, 0)));
    }
}

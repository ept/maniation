package de.kleppmann.maniation;

import de.kleppmann.maniation.dynamics.RigidBody;
import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;

public class Gyroscope extends RigidBody {

    public Gyroscope() {
        super();
        setOrientation(Quaternion.getYRotation(0.2));
        setAngularMomentum(getOrientation().transform(new Vector3D(0, 0, 5)));
    }

    public Vector3D getTorques() {
        Vector3D end = getOrientation().transform(new Vector3D(0, 0, -1));
        return super.getTorques().add(end.cross(new Vector3D(0, 0, 1)));
    }
}

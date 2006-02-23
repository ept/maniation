package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;

public interface Body extends GeneralizedBody {
    Vector3D getLocation();
    Quaternion getOrientation();
}

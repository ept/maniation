package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;

public interface Body extends GeneralizedBody {
    
    public interface State extends GeneralizedBody.State {
        Vector3D getCoMPosition();
        Vector3D getCoMVelocity();
        Quaternion getOrientation();
        Vector3D getAngularVelocity();
        Body getOwner();
    }

    Body.State getInitialState();
}

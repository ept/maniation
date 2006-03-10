package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;

public interface Body extends GeneralizedBody {
    
    public interface State extends GeneralizedBody.State {
        Vector3D getLocation();
        Quaternion getOrientation();
    }

    Body.State getInitialState();
}

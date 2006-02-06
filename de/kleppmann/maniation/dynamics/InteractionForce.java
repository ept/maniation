package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.Vector3D;

public interface InteractionForce extends Interaction {
    Vector3D getForceOn(Body b);
    Vector3D getTorqueOn(Body b);
}

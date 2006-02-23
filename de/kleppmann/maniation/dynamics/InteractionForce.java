package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.Vector;

public interface InteractionForce extends Interaction {
    Vector getForceTorque(GeneralizedBody b);
}

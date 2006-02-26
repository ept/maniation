package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.geometry.CollisionVolume;

public interface Collideable {
    void collideWith(RigidBody body, CollisionVolume volume, InteractionList result);
}

package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.geometry.CollisionVolume;

public interface Collideable {
    void collide(Body.State ownState, CollisionVolume partnerVolume, InteractionList result);
}

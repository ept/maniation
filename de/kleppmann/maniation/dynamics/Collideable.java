package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.geometry.AnimateMesh;

public interface Collideable {
    void collide(Body.State ownState, AnimateMesh partner, InteractionList result);
}

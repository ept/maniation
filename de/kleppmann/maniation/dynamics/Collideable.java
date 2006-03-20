package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.geometry.AnimateMesh;

public interface Collideable {
    void collide(GeneralizedBody.State ownState, AnimateMesh partner, InteractionList result);
}

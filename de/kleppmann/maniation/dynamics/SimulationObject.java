package de.kleppmann.maniation.dynamics;

public interface SimulationObject {
    void interaction(SimulationObject partner, InteractionList result, boolean allowReverse);
    void handleInteraction(Interaction action);
}

package de.kleppmann.maniation.dynamics;

public interface SimulationObject {
    
    public interface State {
        SimulationObject getOwner();
    }
    
    State getInitialState();
    void interaction(State ownState, State partnerState, InteractionList result, boolean allowReverse);
    State handleInteraction(State previousState, Interaction action);
}

package de.kleppmann.maniation.dynamics;

public interface SimulationObject {
    
    public interface State {
        SimulationObject getOwner();
        void interaction(State partnerState, InteractionList result, boolean allowReverse);
        State handleInteraction(State state, Interaction action);
    }
    
    State getInitialState();
}

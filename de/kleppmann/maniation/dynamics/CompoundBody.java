package de.kleppmann.maniation.dynamics;

public class CompoundBody implements GeneralizedBody {
    
    private GeneralizedBody[] bodies;
    
    public CompoundBody(GeneralizedBody[] bodies) {
        this.bodies = bodies;
    }

    public StateVector getInitialState() {
        return new StateVector(this, bodies);
    }

    public StateVector handleInteraction(SimulationObject.State previousState, Interaction action) {
        if (!(previousState instanceof StateVector)) throw new IllegalArgumentException();
        StateVector state = (StateVector) previousState;
        if (state.getOwner() != this) throw new IllegalArgumentException();
        return state.handleInteraction(action);
    }

    public void interaction(SimulationObject.State ownState, SimulationObject.State partnerState,
            InteractionList result, boolean allowReverse) {
        if (!(ownState instanceof StateVector)) throw new IllegalArgumentException();
        StateVector state = (StateVector) ownState;
        if (state.getOwner() != this) throw new IllegalArgumentException();
        for (int i=0; i<bodies.length; i++) {
            GeneralizedBody body = bodies[i];
            GeneralizedBody.State bstate = state.getSlice(i);
            body.interaction(bstate, partnerState, result, true);
            for (int j=i+1; j<bodies.length; j++)
                body.interaction(bstate, state.getSlice(j), result, true);
        }
    }
}

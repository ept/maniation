package de.kleppmann.maniation.dynamics;

public class CompoundBody implements GeneralizedBody {
    
    private World world;
    private GeneralizedBody[] bodies;
    
    public CompoundBody(World world, GeneralizedBody[] bodies) {
        this.bodies = bodies;
        this.world = world;
    }
    
    public GeneralizedBody getBody(int index) {
        return bodies[index];
    }
    
    public int getBodies() {
        return bodies.length;
    }
    
    GeneralizedBody[] getBodyArray() {
        return bodies;
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
            // If interacting with the world, we also let all bodies interact amongst each other
            if (partnerState.getOwner() == world) {
                for (int j=i+1; j<bodies.length; j++)
                    body.interaction(bstate, state.getSlice(j), result, true);
            }
        }
    }
}

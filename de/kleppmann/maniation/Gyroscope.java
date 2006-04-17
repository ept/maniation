package de.kleppmann.maniation;

import de.kleppmann.maniation.dynamics.Constraint;
import de.kleppmann.maniation.dynamics.Cylinder;
import de.kleppmann.maniation.dynamics.InteractionList;
import de.kleppmann.maniation.dynamics.NailConstraint;
import de.kleppmann.maniation.dynamics.World;
import de.kleppmann.maniation.dynamics.SimulationObject.State;
import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;

public class Gyroscope extends Cylinder {

    private World world;
    private Constraint nail;
    private Quaternion initialOrientation = Quaternion.fromYRotation(Math.PI/4.0);
    
    public Gyroscope(World world) {
        // rotation about axis: 20 per second
        // expected precession: once in 8 seconds
        super(new Vector3D(0, 0, 1), Math.sqrt(0.0981)/Math.PI, /*0.005*/ Math.sqrt(0.2943)/Math.PI, 0.1);
        this.world = world;
        nail = new NailConstraint(world, this, new Vector3D(0, 0, -0.05), new Vector3D(0, 0, 0));
    }

    @Override
    protected Vector3D getInitialAngularMomentum() {
        return initialOrientation.transform(new Vector3D(0, 0, 0.1962/Math.PI));
    }

    @Override
    protected Quaternion getInitialOrientation() {
        return initialOrientation;
    }

    @Override
    protected Vector3D getInitialPosition() {
        return initialOrientation.transform(new Vector3D(0, 0, 0.05));
    }

    @Override
    public void interaction(State ownState, State partnerState, InteractionList result, boolean allowReverse) {
        super.interaction(ownState, partnerState, result, allowReverse);
        if (partnerState.getOwner() == world) result.addInteraction(nail);
    }
}

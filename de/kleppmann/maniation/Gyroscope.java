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
    private Quaternion initialOrientation = Quaternion.fromYRotation(0.2);
    
    public Gyroscope(World world) {
        super(new Vector3D(0, 0, 1), 4.0, 0.2, 1.0);
        this.world = world;
        nail = new NailConstraint(world, this, new Vector3D(0, 0, -1), new Vector3D(0, 0, 0));
    }

    @Override
    protected Vector3D getInitialAngularMomentum() {
        return initialOrientation.transform(new Vector3D(0, 0, 5));
    }

    @Override
    protected Quaternion getInitialOrientation() {
        return initialOrientation;
    }

    @Override
    protected Vector3D getInitialPosition() {
        return initialOrientation.transform(new Vector3D(0, 0, 1));
    }

    @Override
    public void interaction(State ownState, State partnerState, InteractionList result, boolean allowReverse) {
        super.interaction(ownState, partnerState, result, allowReverse);
        if (partnerState.getOwner() == world) result.addInteraction(nail);
    }
}

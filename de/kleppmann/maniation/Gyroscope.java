package de.kleppmann.maniation;

import de.kleppmann.maniation.dynamics.Constraint;
import de.kleppmann.maniation.dynamics.Cylinder;
import de.kleppmann.maniation.dynamics.InteractionList;
import de.kleppmann.maniation.dynamics.NailConstraint;
import de.kleppmann.maniation.dynamics.SimulationObject;
import de.kleppmann.maniation.dynamics.World;
import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;

public class Gyroscope extends Cylinder {

    private World world;
    private Constraint nail;
    
    public Gyroscope(World world) {
        super(new Vector3D(0, 0, 1), 4.0, 0.2, 1.0);
        this.world = world;
        setOrientation(Quaternion.fromYRotation(0.2));
        setAngularMomentum(getOrientation().transform(new Vector3D(0, 0, 5)));
        setCoMPosition(getOrientation().transform(new Vector3D(0, 0, 1)));
        nail = new NailConstraint(world, this, getCoMPosition().mult(-1), new Vector3D(0, 0, 0));
    }

    public void interaction(SimulationObject partner, InteractionList result, boolean allowReverse) {
        super.interaction(partner, result, allowReverse);
        if (partner == world) result.addInteraction(nail);
    }
}

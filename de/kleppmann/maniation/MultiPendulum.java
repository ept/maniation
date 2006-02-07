package de.kleppmann.maniation;

import java.util.Map;

import de.kleppmann.maniation.dynamics.Constraint;
import de.kleppmann.maniation.dynamics.Cylinder;
import de.kleppmann.maniation.dynamics.InteractionList;
import de.kleppmann.maniation.dynamics.JointConstraint;
import de.kleppmann.maniation.dynamics.NailConstraint;
import de.kleppmann.maniation.dynamics.Simulation;
import de.kleppmann.maniation.dynamics.SimulationObject;
import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;

public class MultiPendulum extends Cylinder {
    
    private Map<SimulationObject,Constraint> constraints;

    private MultiPendulum(int segment) {
        super(0.05, 2.0, 1.0);
        if (segment == 0) {
            setCoMPosition(new Vector3D(0.5*Math.sqrt(2.0), 0.0, -0.5*Math.sqrt(2.0)));
            setOrientation(Quaternion.getYRotation(-Math.PI/4.0));
        } else {
            setCoMPosition(new Vector3D(Math.sqrt(2.0), 0.0, 1.0 - Math.sqrt(2.0) - 2.0*segment));
        }
    }
    
    public void interaction(SimulationObject partner, InteractionList result, boolean allowReverse) {
        super.interaction(partner, result, allowReverse);
        Constraint c = constraints.get(partner);
        if (c != null) result.addInteraction(c);
    }


    public static void setup(Simulation simulation, int segments) {
        MultiPendulum[] bodies = new MultiPendulum[segments];
        for (int i=0; i<segments; i++) {
            bodies[i] = new MultiPendulum(i);
            simulation.addBody(bodies[i]);
        }
        for (int i=0; i<segments; i++) {
            Map<SimulationObject,Constraint> map = new java.util.HashMap<SimulationObject,Constraint>();
            bodies[i].constraints = map;
            if (i == 0) {
                map.put(simulation.getWorld(), new NailConstraint(simulation.getWorld(),
                        bodies[0], new Vector3D(0, 0, 1), new Vector3D(0, 0, 0)));
            } else {
                Constraint joint = new JointConstraint(bodies[i-1], new Vector3D(0, 0, -1),
                        bodies[i], new Vector3D(0, 0, 1));
                map.put(bodies[i-1], joint);
                bodies[i-1].constraints.put(bodies[i], joint);
            }
        }
    }
}

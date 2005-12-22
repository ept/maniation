package de.kleppmann.maniation;

import de.kleppmann.maniation.dynamics.ConstrainedRigidBodies;
import de.kleppmann.maniation.maths.ODESolver;
import de.kleppmann.maniation.maths.RungeKutta;

public class TestDynamics {

    public TestDynamics() {
        super();
        ConstrainedRigidBodies crb = new ConstrainedRigidBodies();
        Gyroscope gyro = new Gyroscope();
        crb.addBody(gyro);
        ODESolver solver = new RungeKutta(crb, 0.1);
        solver.solveUpTo(10.0);
    }

    public static void main(String[] args) {
        new TestDynamics();
    }

}

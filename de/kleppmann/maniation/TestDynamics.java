package de.kleppmann.maniation;

import de.kleppmann.maniation.dynamics.ConstrainedRigidBodies;

public class TestDynamics {

    public TestDynamics() {
        super();
        ConstrainedRigidBodies crb = new ConstrainedRigidBodies();
        /*Gyroscope gyro = new Gyroscope();
        crb.addBody(gyro);*/
        /*MultiPendulum.setup(crb, 2);*/
        Centrifuge.setup(crb);
    }

    public static void main(String[] args) {
        new TestDynamics();
    }

}

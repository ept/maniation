package de.kleppmann.maniation;

import de.kleppmann.maniation.dynamics.Simulation;

public class TestDynamics {

    public TestDynamics() {
        super();
        Simulation sim = new Simulation();
        MultiPendulum.setup(sim, 2);
        sim.run(2.0);
    }

    public static void main(String[] args) {
        new TestDynamics();
    }
}

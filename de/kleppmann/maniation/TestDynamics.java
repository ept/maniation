package de.kleppmann.maniation;

import de.kleppmann.maniation.dynamics.Simulation;

public class TestDynamics {

    public TestDynamics() {
        super();
        Simulation sim = new Simulation();
        //MultiPendulum.setup(sim, 2);
        new NewtonsCradle(sim);
        sim.run(1.0);
    }

    public static void main(String[] args) {
        new TestDynamics();
    }
}

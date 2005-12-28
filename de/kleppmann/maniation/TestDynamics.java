package de.kleppmann.maniation;

import java.io.FileWriter;
import java.io.IOException;

import de.kleppmann.maniation.dynamics.ConstrainedRigidBodies;
import de.kleppmann.maniation.maths.RungeKutta;

public class TestDynamics {

    public TestDynamics() {
        super();
        ConstrainedRigidBodies crb = new ConstrainedRigidBodies();
        Gyroscope gyro = new Gyroscope();
        crb.addBody(gyro);
        RungeKutta solver = new RungeKutta(crb, 0.1);
        solver.solveUpTo(50.0);
        try {
            FileWriter writer = new FileWriter("/home/martin/graphics/maniation/matlab/javadata");
            writer.write("# name: data\n");
            writer.write("# type: matrix\n");
            writer.write("# rows: " + solver.getLog().size() + "\n");
            writer.write("# columns: 13\n");
            for (String line : solver.getLog()) writer.write(line + "\n");
            writer.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void main(String[] args) {
        new TestDynamics();
    }

}

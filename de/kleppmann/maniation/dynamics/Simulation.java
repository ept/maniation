package de.kleppmann.maniation.dynamics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import de.kleppmann.maniation.maths.ODE;
import de.kleppmann.maniation.maths.ODESolver;
import de.kleppmann.maniation.maths.RungeKutta;
import de.kleppmann.maniation.maths.Vector;

public class Simulation {
    
    private List<SimulationObject> objects = new java.util.ArrayList<SimulationObject>();
    private StateVector state = new StateVector(null, false), stateDot = new StateVector(null, true);
    private List<String> log = new java.util.ArrayList<String>();
    
    public void addObject(SimulationObject object) {
        objects.add(object);
        SimulationObject[] array = new SimulationObject[objects.size()];
        array = objects.toArray(array);
        state = new StateVector(array, false);
        stateDot = new StateVector(array, true);
    }
    
    private void setTime(double time) {
        for (SimulationObject obj : objects) obj.setSimulationTime(time);
    }
    
    public void run(double time) {
        ODESolver solver = new RungeKutta(new DifferentialEquation(), 0.1);
        solver.solve(0.0, time);
        try {
            FileWriter writer = new FileWriter("/home/martin/graphics/maniation/matlab/javadata");
            writer.write("# name: data\n");
            writer.write("# type: matrix\n");
            writer.write("# rows: " + log.size() + "\n");
            writer.write("# columns: " + state.getDimension() + "\n");
            for (String line : log) writer.write(line + "\n");
            writer.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    
    private class DifferentialEquation implements ODE {
        public Vector derivative(double time, Vector state) {
            if (!(state instanceof StateVector)) throw new IllegalArgumentException();
            setTime(time);
            ((StateVector) state).apply();
            return stateDot;
        }

        public Vector getInitial() {
            return state;
        }

        public void timeStepCompleted(double time, Vector state) {
            log.add(state.toString());
        }
    }
}

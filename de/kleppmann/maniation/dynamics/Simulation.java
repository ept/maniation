package de.kleppmann.maniation.dynamics;

import java.util.List;

import de.kleppmann.maniation.maths.ODE;
import de.kleppmann.maniation.maths.Vector;

public class Simulation {
    
    private List<SimulationObject> objects = new java.util.ArrayList<SimulationObject>();
    private StateVector state = new StateVector(null, false), stateDot = new StateVector(null, true);
    
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
    }
}

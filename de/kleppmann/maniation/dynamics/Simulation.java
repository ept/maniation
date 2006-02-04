package de.kleppmann.maniation.dynamics;

import java.util.List;

import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.VectorImpl;

public class Simulation {
    
    private List<SimulationObject> objects = new java.util.ArrayList<SimulationObject>();
    private int[] stateOffsets = {0};
    private State state = new State(false), stateDot = new State(true);
    
    private void updateObjects() {
        int i=0, j=0;
        stateOffsets = new int[objects.size() + 1];
        for (SimulationObject obj : objects) {
            stateOffsets[i] = j;
            i++; j += obj.getState(false).getDimension();
        }
        stateOffsets[i] = j;
    }
    
    public void addObject(SimulationObject object) {
        objects.add(object);
        updateObjects();
    }

    
    private class State extends VectorImpl {
        private boolean rateOfChange;
        
        State(boolean rateOfChange) {
            super(null);
            this.rateOfChange = rateOfChange;
        }

        public int getDimension() {
            return stateOffsets[stateOffsets.length - 1];
        }

        public double getComponent(int index) {
            // Naive linear search. Replace this by binary chop.
            int i = stateOffsets.length - 1;
            while (stateOffsets[i] > index) i--;
            return objects.get(i).getState(rateOfChange).getComponent(index - stateOffsets[i]);
        }

        public Vector add(Vector v) {
            // TODO Auto-generated method stub
            return null;
        }

        public Vector subtract(Vector v) {
            // TODO Auto-generated method stub
            return null;
        }

        public Vector mult(double scalar) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}

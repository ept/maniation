package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.Quaternion;

class StateVectorModified extends StateVector {
    
    enum Operation { MULT_COMPONENTS, ADD, SUBTRACT };
    
    private double[] state;

    public StateVectorModified(StateVector origin, Operation operation, StateVector operand) {
        super(origin.getScene());
        if (origin.getDimension() != operand.getDimension()) throw new IllegalArgumentException();
        if (origin.isDerivative() && !operand.isDerivative()) {
            StateVector tmp = origin; origin = operand; operand = tmp;
        }
        setDerivative(origin.isDerivative());
        this.state = new double[super.getDimension()];
        switch (operation) {
        case MULT_COMPONENTS:
            for (int i=0; i<state.length; i++)
                state[i] = origin.getComponent(i) * operand.getComponent(i);
            break;
        case ADD:
            for (int i=0; i<state.length; i++)
                state[i] = origin.getComponent(i) + operand.getComponent(i);
            break;
        case SUBTRACT:
            for (int i=0; i<state.length; i++)
                state[i] = origin.getComponent(i) - operand.getComponent(i);
            break;
        }
        if (!origin.isDerivative() && (operation == Operation.ADD ||
                operation == Operation.SUBTRACT)) {
            for (int i=0; i<state.length/13; i++) {
                Quaternion q1 = new Quaternion(origin.getComponent(13*i+3),
                        origin.getComponent(13*i+4), origin.getComponent(13*i+5),
                        origin.getComponent(13*i+6));
                Quaternion q2;
                if (operation == Operation.ADD)
                    q2 = new Quaternion(operand.getComponent(13*i+3),
                        operand.getComponent(13*i+4), operand.getComponent(13*i+5),
                        operand.getComponent(13*i+6)); else
                    q2 = new Quaternion(-operand.getComponent(13*i+3),
                        -operand.getComponent(13*i+4), -operand.getComponent(13*i+5),
                        -operand.getComponent(13*i+6));
                Quaternion q = q1.quergs(q2);
                state[13*i+3] = q.getW();
                state[13*i+4] = q.getX();
                state[13*i+5] = q.getY();
                state[13*i+6] = q.getZ();
            }
        }        
    }

    public int getDimension() {
        return state.length;
    }

    public double getComponent(int index) {
        return state[index];
    }
}

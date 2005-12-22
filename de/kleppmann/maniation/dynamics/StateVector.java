package de.kleppmann.maniation.dynamics;

import java.text.DecimalFormat;
import java.util.List;

import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.Vector3D;

public class StateVector implements Vector {
    
    private List<RigidBody> bodies = new java.util.ArrayList<RigidBody>();
    private boolean derivative = false;
    
    void applyProperties(StateVector source) {
        this.bodies = source.bodies;
        this.derivative = source.derivative;
    }
    
    List<RigidBody> getBodies() {
        return bodies;
    }
    
    public boolean isDerivative() {
        return derivative;
    }
    
    void setDerivative(boolean isDerivative) {
        derivative = isDerivative;
    }
    
    StateVector getDerivative() {
        StateVector result = new StateVector();
        result.applyProperties(this);
        result.setDerivative(true);
        return result;
    }

    public void addBody(RigidBody body) {
        bodies.add(body);
    }

    public int getDimension() {
        return 13*bodies.size();
    }

    public double getComponent(int index) {
        RigidBody body = bodies.get(index/13);
        int n = index - index/13;
        if (!derivative) {
            if (n < 3) return body.getCoMPosition().getComponent(n);
            if (n == 3) return body.getOrientation().getW();
            if (n == 4) return body.getOrientation().getX();
            if (n == 5) return body.getOrientation().getY();
            if (n == 6) return body.getOrientation().getZ();
            if (n < 10) return body.getLinearMomentum().getComponent(n - 7);
            return body.getAngularMomentum().getComponent(n - 10);
        } else {
            if (n < 3) return body.getCoMVelocity().getComponent(n);
            if (n == 3) return body.getRateOfRotation().getW();
            if (n == 4) return body.getRateOfRotation().getX();
            if (n == 5) return body.getRateOfRotation().getY();
            if (n == 6) return body.getRateOfRotation().getZ();
            if (n < 10) return body.getForces().getComponent(n - 7);
            return body.getTorques().getComponent(n - 10);
        }
    }

    public Vector mult(double scalar) {
        return new StateVectorScaled(this, scalar);
    }

    public double mult(Vector v) {
        throw new UnsupportedOperationException();
    }

    public Vector multComponents(Vector v) {
        if (!(v instanceof StateVector)) throw new IllegalArgumentException();
        return new StateVectorModified(this,
                StateVectorModified.Operation.MULT_COMPONENTS, (StateVector) v);
    }

    public Vector add(Vector v) {
        if (!(v instanceof StateVector)) throw new IllegalArgumentException();
        return new StateVectorModified(this,
                StateVectorModified.Operation.ADD, (StateVector) v);
    }

    public Vector subtract(Vector v) {
        if (!(v instanceof StateVector)) throw new IllegalArgumentException();
        return new StateVectorModified(this,
                StateVectorModified.Operation.SUBTRACT, (StateVector) v);
    }

    public void toDoubleArray(double[] array, int offset) {
        for (int i=0; i<getDimension(); i++) array[i+offset] = getComponent(i);
    }
    
    public void updateBodies() {
        int i=0;
        for (RigidBody body : bodies) {
            body.setCoMPosition(new Vector3D(getComponent(i), getComponent(i+1), getComponent(i+2)));
            body.setOrientation(new Quaternion(getComponent(i+3), getComponent(i+4), 
                    getComponent(i+5), getComponent(i+6)));
            body.setLinearMomentum(new Vector3D(getComponent(i+7), getComponent(i+8), getComponent(i+9)));
            body.setAngularMomentum(new Vector3D(getComponent(i+10), getComponent(i+11), getComponent(i+12)));
            i += 13;
        }
    }
    
    public String toString() {
        DecimalFormat format = new DecimalFormat("######0.00000");
        String result = "";
        for (int i=0; i<getDimension(); i++) {
            if (!result.equals("")) result += ", ";
            result += format.format(getComponent(i));
        }
        return result + ";";
    }
}

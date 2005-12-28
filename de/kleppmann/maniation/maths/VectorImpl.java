package de.kleppmann.maniation.maths;

import java.text.DecimalFormat;

public class VectorImpl implements Vector {

    private double values[];
    
    public VectorImpl(int dimension) {
        values = new double[dimension];
        for (int i=0; i<dimension; i++) values[i] = 0.0;
    }
    
    public VectorImpl(double[] values) {
        this.values = values;
    }

    public String toString() {
        DecimalFormat format = new DecimalFormat("######0.00000");
        String result = "";
        for (int i=0; i<getDimension(); i++) {
            if (!result.equals("")) result += ", ";
            result += format.format(getComponent(i));
        }
        return "Vector(" + result + ")";
    }

    public int getDimension() {
        return values.length;
    }
    
    public double getComponent(int index) {
        return values[index];
    }
    
    public Vector mult(double scalar) {
        double[] newv = new double[getDimension()];
        for (int i=0; i<getDimension(); i++) newv[i] = scalar*getComponent(i);
        return new VectorImpl(newv);
    }
    
    public double mult(Vector v) {
        if (v.getDimension() != getDimension()) throw new IllegalArgumentException();
        double result = 0.0;
        for (int i=0; i<getDimension(); i++)
            result += this.getComponent(i) * v.getComponent(i);
        return result;
    }
    
    public Vector multComponents(Vector v) {
        if (v.getDimension() != getDimension()) throw new IllegalArgumentException();
        double[] newv = new double[getDimension()];
        for (int i=0; i<getDimension(); i++)
            newv[i] = this.getComponent(i) * v.getComponent(i);
        return new VectorImpl(newv);
    }

    public Vector add(Vector v) {
        if (v.getDimension() != getDimension()) throw new IllegalArgumentException();
        double[] newv = new double[getDimension()];
        for (int i=0; i<getDimension(); i++)
            newv[i] = this.getComponent(i) + v.getComponent(i);
        return new VectorImpl(newv);
    }
    
    public Vector subtract(Vector v) {
        if (v.getDimension() != getDimension()) throw new IllegalArgumentException();
        double[] newv = new double[getDimension()];
        for (int i=0; i<getDimension(); i++)
            newv[i] = this.getComponent(i) - v.getComponent(i);
        return new VectorImpl(newv);
    }
    
    public void toDoubleArray(double[] array, int offset) {
        for (int i=0; i<getDimension(); i++) array[offset+i] = getComponent(i);
    }
}

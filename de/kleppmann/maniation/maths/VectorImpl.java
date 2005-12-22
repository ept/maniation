package de.kleppmann.maniation.maths;

import java.text.DecimalFormat;

public class VectorImpl implements Vector {

    private double values[];
    
    public VectorImpl(double[] values) {
        this.values = values;
    }

    public String toString() {
        DecimalFormat format = new DecimalFormat("######0.00000");
        String result = "";
        for (int i=0; i<values.length; i++) {
            if (!result.equals("")) result += ", ";
            result += format.format(values[i]);
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
        for (int i=0; i<getDimension(); i++) newv[i] = scalar*values[i];
        return new VectorImpl(newv);
    }
    
    public double mult(Vector v) {
        if (v.getDimension() != getDimension()) throw new IllegalArgumentException();
        double result = 0.0;
        for (int i=0; i<getDimension(); i++) result += values[i] * v.getComponent(i);
        return result;
    }
    
    public Vector multComponents(Vector v) {
        if (v.getDimension() != getDimension()) throw new IllegalArgumentException();
        double[] newv = new double[getDimension()];
        for (int i=0; i<getDimension(); i++) newv[i] = values[i] * v.getComponent(i);
        return new VectorImpl(newv);
    }

    public Vector add(Vector v) {
        if (v.getDimension() != getDimension()) throw new IllegalArgumentException();
        double[] newv = new double[getDimension()];
        for (int i=0; i<getDimension(); i++) newv[i] = values[i] + v.getComponent(i);
        return new VectorImpl(newv);
    }
    
    public Vector subtract(Vector v) {
        if (v.getDimension() != getDimension()) throw new IllegalArgumentException();
        double[] newv = new double[getDimension()];
        for (int i=0; i<getDimension(); i++) newv[i] = values[i] - v.getComponent(i);
        return new VectorImpl(newv);
    }
    
    public void toDoubleArray(double[] array, int offset) {
        for (int i=0; i<getDimension(); i++) array[offset+i] = values[i];
    }
}

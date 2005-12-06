package de.kleppmann.maniation.maths;

import java.text.DecimalFormat;


public class Vector {
    
    private double values[];
    
    private Vector() {}

    public Vector(double x, double y, double z) {
        values = new double[3];
        values[0] = x;
        values[1] = y;
        values[2] = z;
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
    
    public double getElement(int index) {
        return values[index];
    }
    
    public Vector mult(double scalar) {
        Vector v = new Vector();
        v.values = new double[getDimension()];
        for (int i=0; i<getDimension(); i++) v.values[i] = scalar*values[i];
        return v;
    }
    
    public double mult(Vector v) {
        if (v.getDimension() != getDimension()) throw new IllegalArgumentException();
        double result = 0.0;
        for (int i=0; i<getDimension(); i++) result += values[i]*v.values[i];
        return result;
    }
    
    public Vector add(Vector v) {
        if (v.getDimension() != getDimension()) throw new IllegalArgumentException();
        Vector result = new Vector();
        result.values = new double[getDimension()];
        for (int i=0; i<getDimension(); i++) result.values[i] = values[i] + v.values[i];
        return result;
    }
    
    public Vector subtract(Vector v) {
        if (v.getDimension() != getDimension()) throw new IllegalArgumentException();
        Vector result = new Vector();
        result.values = new double[getDimension()];
        for (int i=0; i<getDimension(); i++) result.values[i] = values[i] - v.values[i];
        return result;
    }
    
    public void toDoubleArray(double[] array, int offset) {
        for (int i=0; i<getDimension(); i++) array[offset+i] = values[i];
    }
}

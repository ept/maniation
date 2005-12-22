package de.kleppmann.maniation.maths;

public interface Vector {
    int getDimension();
    double getComponent(int index);
    Vector mult(double scalar);
    double mult(Vector v);
    Vector multComponents(Vector v);
    Vector add(Vector v);
    Vector subtract(Vector v);
    void toDoubleArray(double[] array, int offset);
}

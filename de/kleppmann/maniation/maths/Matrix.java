package de.kleppmann.maniation.maths;

public interface Matrix {
    int getRows();
    int getColumns();
    Matrix mult(double scalar);
    Matrix mult(Matrix other);
    Vector mult(Vector other);
    Matrix add(Matrix other);
    Matrix subtract(Matrix other);
}

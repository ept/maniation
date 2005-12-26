package de.kleppmann.maniation.maths;

public interface Matrix {
    int getRows();
    int getColumns();
    double getComponent(int row, int column);
    Matrix transpose();
    Matrix inverse();
    Matrix mult(double scalar);
    Matrix mult(Matrix other);
    Vector mult(Vector vec);
    Matrix add(Matrix other);
    Matrix subtract(Matrix other);
}

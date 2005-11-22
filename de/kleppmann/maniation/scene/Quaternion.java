package de.kleppmann.maniation.scene;

public interface Quaternion {
    de.kleppmann.maniation.maths.Quaternion getValue();
    double getX();
    void setX(double x);
    double getY();
    void setY(double y);
    double getZ();
    void setZ(double z);
    double getW();
    void setW(double w);
}

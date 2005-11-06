package de.kleppmann.maniation.scene;

public interface AxisConstraint {
    double getMaxComfortable();
    void setMaxComfortable(double maxComfortable);
    double getMinComfortable();
    void setMinComfortable(double minComfortable);
    double getMaxExtreme();
    void setMaxExtreme(double maxExtreme);
    double getMinExtreme();
    void setMinExtreme(double minExtreme);
}

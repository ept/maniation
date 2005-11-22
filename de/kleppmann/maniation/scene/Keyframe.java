package de.kleppmann.maniation.scene;

public interface Keyframe {
    double getTime();
    void setTime(double time);
    de.kleppmann.maniation.scene.Quaternion getRotation();
    void setRotation(de.kleppmann.maniation.scene.Quaternion rotation);
}

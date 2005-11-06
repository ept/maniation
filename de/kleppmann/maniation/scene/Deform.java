package de.kleppmann.maniation.scene;

public interface Deform {
    void setBone(de.kleppmann.maniation.scene.Bone bone);
    de.kleppmann.maniation.scene.Bone getBone();
    double getWeight();
    void setWeight(double weight);
}

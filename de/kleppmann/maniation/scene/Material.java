package de.kleppmann.maniation.scene;

public interface Material {
    java.lang.String getId();
    void setId(java.lang.String id);
    double getShininess();
    void setShininess(double shininess);
    double getDensity();
    void setDensity(double density);
    boolean isHollow();
    void setHollow(boolean hollow);
    double getElasticity();
    void setElasticity(double elasticity);
    double getStaticFriction();
    void setStaticFriction(double staticFriction);
    double getSlidingFriction();
    void setSlidingFriction(double slidingFriction);
    de.kleppmann.maniation.scene.Colour getAmbient();
    void setAmbient(de.kleppmann.maniation.scene.Colour ambient);
    de.kleppmann.maniation.scene.Colour getDiffuse();
    void setDiffuse(de.kleppmann.maniation.scene.Colour diffuse);
    de.kleppmann.maniation.scene.Colour getSpecular();
    void setSpecular(de.kleppmann.maniation.scene.Colour specular);
    de.kleppmann.maniation.scene.Colour getEmissive();
    void setEmissive(de.kleppmann.maniation.scene.Colour emissive);
}

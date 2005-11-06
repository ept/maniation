package de.kleppmann.maniation.scene;

public interface Vertex {
    java.lang.String getId();
    void setId(java.lang.String id);
    de.kleppmann.maniation.scene.Vector getPosition();
    void setPosition(de.kleppmann.maniation.scene.Vector position);
    de.kleppmann.maniation.scene.Vector getNormal();
    void setNormal(de.kleppmann.maniation.scene.Vector normal);
    java.util.List<de.kleppmann.maniation.scene.Deform> getDeforms();
}

package de.kleppmann.maniation.scene;

public interface Body {
    java.lang.String getName();
    void setName(java.lang.String name);
    de.kleppmann.maniation.scene.Mesh getMesh();
    void setMesh(de.kleppmann.maniation.scene.Mesh mesh);
    boolean isMobile();
    void setMobile(boolean mobile);
    de.kleppmann.maniation.scene.Vector getLocation();
    void setLocation(de.kleppmann.maniation.scene.Vector location);
    de.kleppmann.maniation.scene.Quaternion getOrientation();
    void setOrientation(de.kleppmann.maniation.scene.Quaternion orientation);
    de.kleppmann.maniation.scene.Vector getAxis();
    void setAxis(de.kleppmann.maniation.scene.Vector axis);
}

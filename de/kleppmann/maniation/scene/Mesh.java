package de.kleppmann.maniation.scene;

public interface Mesh {
    java.lang.String getId();
    void setId(java.lang.String id);
    void setSkeleton(de.kleppmann.maniation.scene.Skeleton skeleton);
    de.kleppmann.maniation.scene.Skeleton getSkeleton();
    void setMaterial(de.kleppmann.maniation.scene.Material material);
    de.kleppmann.maniation.scene.Material getMaterial();
    java.util.List<de.kleppmann.maniation.scene.Vertex> getVertices();
    java.util.List<de.kleppmann.maniation.scene.Face> getFaces();
    javax.media.j3d.Node getJava3D();
}

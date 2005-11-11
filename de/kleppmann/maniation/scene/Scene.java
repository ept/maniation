package de.kleppmann.maniation.scene;

public interface Scene {
    java.util.List<de.kleppmann.maniation.scene.Material> getMaterials();
    java.util.List<de.kleppmann.maniation.scene.Skeleton> getSkeletons();
    java.util.List<de.kleppmann.maniation.scene.Mesh> getMeshes();
}

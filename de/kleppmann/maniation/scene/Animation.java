package de.kleppmann.maniation.scene;

public interface Animation {
    double getStart();
    void setStart(double start);
    double getFinish();
    void setFinish(double finish);
    boolean isLoop();
    void setLoop(boolean loop);
    java.util.List<de.kleppmann.maniation.scene.Keyframe> getKeyframes();
}

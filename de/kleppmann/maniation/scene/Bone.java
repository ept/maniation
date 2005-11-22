package de.kleppmann.maniation.scene;

public interface Bone {
    de.kleppmann.maniation.maths.Quaternion getRotationAt(double time);
    java.lang.String getId();
    void setId(java.lang.String id);
    java.lang.String getName();
    void setName(java.lang.String name);
    void setParentBone(de.kleppmann.maniation.scene.Bone parentBone);
    de.kleppmann.maniation.scene.Bone getParentBone();
    de.kleppmann.maniation.scene.Vector getBase();
    void setBase(de.kleppmann.maniation.scene.Vector base);
    de.kleppmann.maniation.scene.Quaternion getOrientation();
    void setOrientation(de.kleppmann.maniation.scene.Quaternion orientation);
    de.kleppmann.maniation.scene.Vector getTranslationToLocal();
    void setTranslationToLocal(de.kleppmann.maniation.scene.Vector translationToLocal);
    de.kleppmann.maniation.scene.Quaternion getRotationToLocal();
    void setRotationToLocal(de.kleppmann.maniation.scene.Quaternion rotationToLocal);
    de.kleppmann.maniation.scene.AxisConstraint getXAxis();
    void setXAxis(de.kleppmann.maniation.scene.AxisConstraint xAxis);
    de.kleppmann.maniation.scene.AxisConstraint getYAxis();
    void setYAxis(de.kleppmann.maniation.scene.AxisConstraint yAxis);
    de.kleppmann.maniation.scene.AxisConstraint getZAxis();
    void setZAxis(de.kleppmann.maniation.scene.AxisConstraint zAxis);
    de.kleppmann.maniation.scene.Animation getAnimation();
    void setAnimation(de.kleppmann.maniation.scene.Animation animation);
}

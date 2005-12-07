package de.kleppmann.maniation.geometry;

import java.util.Enumeration;
import java.util.List;

import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupOnElapsedFrames;

public class GeometryBehaviour extends Behavior {
    private List<AnimateObject> objects = new java.util.ArrayList<AnimateObject>();
    private WakeupOnElapsedFrames w;
    
    public GeometryBehaviour() {
        w = new WakeupOnElapsedFrames(0);
        setSchedulingBounds(new javax.media.j3d.BoundingSphere(
                new javax.vecmath.Point3d(0.0f, 0.0f, 0.0f), 10));
    }
    
    public void addObject(AnimateObject obj) {
        objects.add(obj);
    }
    
    public void initialize() {
        wakeupOn(w);
    }

    public void processStimulus(Enumeration criteria) {
        for (AnimateObject obj : objects) obj.processStimulus();
        wakeupOn(w);
    }
}

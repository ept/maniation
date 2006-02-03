package de.kleppmann.maniation.jointlimit;

import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;

import javax.media.j3d.BoundingSphere;
import javax.vecmath.Point3d;

public class PointPicker extends PickMouseBehavior {
    
    private DisplayWindow display;
    MouseTranslate translate;

    public PointPicker(DisplayWindow display) {
        super(display.getCanvas3D(), display.getBranchGroup(),
                new BoundingSphere(new Point3d(0f, 0f, 0f), 10));
        this.display = display;
        this.setSchedulingBounds(new BoundingSphere(new Point3d(0f, 0f, 0f), 10));
    }

    public void updateScene(int xpos, int ypos) {
        if (!mevent.isAltDown() && mevent.isMetaDown()) {
            pickCanvas.setShapeLocation(xpos, ypos);
            PickResult pr = pickCanvas.pickClosest();
            if (pr != null) {
                for (int i=0; i<pr.numIntersections(); i++) {
                    int[] indices = pr.getIntersection(i).getPrimitiveCoordinateIndices();
                    for (int j : indices) display.setSelectedPoint(j);
                }
            }
        }
    }
}

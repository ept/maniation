package de.kleppmann.maniation.jointlimit;

import com.sun.j3d.utils.picking.*;
import com.sun.j3d.utils.picking.behaviors.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;

public class PointPicker extends PickMouseBehavior implements MouseBehaviorCallback {
    MouseTranslate translate;
    private PickingCallback callback = null;
    private TransformGroup currentTG;

    public PointPicker(BranchGroup root, Canvas3D canvas, Bounds bounds) {
        super(canvas, root, bounds);
/*        translate = new MouseTranslate(MouseBehavior.MANUAL_WAKEUP);
        translate.setTransformGroup(currGrp);
        currGrp.addChild(translate);
        translate.setSchedulingBounds(bounds);*/
        this.setSchedulingBounds(bounds);
    }

    public void updateScene(int xpos, int ypos) {
        //TransformGroup tg = null;
                
        if (!mevent.isAltDown() && mevent.isMetaDown()) {
            
            System.out.println("updateScene(" + xpos + ", " + ypos + ")");
            pickCanvas.setShapeLocation(xpos, ypos);
            PickResult pr = pickCanvas.pickClosest();
            if ((pr != null)/* &&
                    ((tg = (TransformGroup)pr.getNode(PickResult.TRANSFORM_GROUP)) != null) &&
                    (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_READ)) && 
                    (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_WRITE))*/) {

                System.out.println(pr);
                /*translate.setTransformGroup(tg);
                translate.wakeup();
                currentTG = tg;*/
                //freePickResult(pr);
            } else if (callback != null) callback.transformChanged(PickingCallback.NO_PICK, null);
        }
    }

    public void transformChanged(int type, Transform3D transform) {
        callback.transformChanged(PickingCallback.TRANSLATE, currentTG);
    }

    public void setupCallback(PickingCallback callback) {
        this.callback = callback;
        if (callback == null) translate.setupCallback(null);
        else translate.setupCallback(this);
    }
}

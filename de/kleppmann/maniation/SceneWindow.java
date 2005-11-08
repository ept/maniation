package de.kleppmann.maniation;

import java.awt.Dimension;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Light;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import com.sun.j3d.utils.universe.SimpleUniverse;
import de.kleppmann.maniation.scene.Scene;

public class SceneWindow extends JFrame {

    private static final long serialVersionUID = 0;
    private Scene scene;
        
    public SceneWindow(Scene scene) {
        super("Part II project");
        this.scene = scene;
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas.setPreferredSize(new Dimension(800, 600));
        getContentPane().add(canvas);
        SimpleUniverse universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();
        TransformGroup tg = new TransformGroup(getSceneTransform());
        tg.addChild(scene.getJava3D());
        BranchGroup bg = new BranchGroup();
        bg.addChild(tg);
        bg.addChild(getLight1());
        bg.addChild(getLight2());
        javax.media.j3d.Background backgnd = new javax.media.j3d.Background();
        backgnd.setColor(new Color3f(0.5f, 0.5f, 0.5f));
        backgnd.setApplicationBounds(new javax.media.j3d.BoundingSphere(
                new javax.vecmath.Point3d(0.0f, 0.0f, 0.0f), 10));
        bg.addChild(backgnd);
        bg.compile();
        universe.addBranchGraph(bg);
        pack();
        setVisible(true);
    }
    
    private Transform3D getSceneTransform() {
        Transform3D rotX = new Transform3D();
        Transform3D rotZ = new Transform3D();
        rotX.rotX(-Math.PI/2.0);
        rotZ.rotZ(-Math.PI/6.0);
        rotX.mul(rotZ);
        rotX.setTranslation(new Vector3d(0.0f, -0.8f, -1.0f));
        return rotX;
    }
    
    private Light getLight1() {
        AmbientLight light = new AmbientLight(true, new Color3f(0.25f, 0.25f, 0.25f));
        light.setInfluencingBounds(new BoundingSphere(new Point3d(0.0f, 0.0f, 0.0f), 10));
        return light;
    }
    
    private Light getLight2() {
        DirectionalLight light = new DirectionalLight();
        light.setInfluencingBounds(new BoundingSphere(new Point3d(0.0f, 0.0f, 0.0f), 10));
        Vector3f dir = new Vector3f(0.0f, -1.0f, -0.5f);
        dir.normalize();
        light.setDirection(dir);
        light.setColor(new Color3f(0.4f, 0.4f, 0.5f));
        return light;
    }
    
    public Scene getScene() {
        return scene;
    }
}

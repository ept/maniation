package de.kleppmann.maniation;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import com.sun.j3d.utils.universe.SimpleUniverse;

import de.kleppmann.maniation.geometry.AnimateObject;
//import de.kleppmann.maniation.geometry.AnimateSkeleton;
import de.kleppmann.maniation.geometry.GeometryBehaviour;
import de.kleppmann.maniation.geometry.ArticulatedMesh;
import de.kleppmann.maniation.scene.Body;
import de.kleppmann.maniation.scene.Scene;

public class SceneWindow extends JFrame {

    private static final long serialVersionUID = 0;
    private Scene scene;
    private TransformGroup sceneAsJava3D;
    private SimpleUniverse universe;
        
    public SceneWindow(Scene scene) {
        super("Part II project");
        this.scene = scene;
        initialize();
        // Set up Java3D scene branch
        BranchGroup bg = new BranchGroup();
        buildScene(bg);
        addBackground(bg);
        addLights(bg);
        addAnimation(bg);
        bg.compile();
        universe.addBranchGraph(bg);
        pack();
        setVisible(true);
    }
    
    private void initialize() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }            
        });
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas.setPreferredSize(new Dimension(800, 600));
        getContentPane().add(canvas);
        universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();        
    }
    
    private void buildScene(BranchGroup bg) {
        sceneAsJava3D = new javax.media.j3d.TransformGroup();
        sceneAsJava3D.setCapability(javax.media.j3d.TransformGroup.ALLOW_TRANSFORM_WRITE);
        TransformGroup tg = new TransformGroup(getSceneTransform());
        tg.addChild(sceneAsJava3D);
        bg.addChild(tg);
        GeometryBehaviour behaviour = new GeometryBehaviour();
        bg.addChild(behaviour);
        for (Body body : scene.getBodies()) {
            AnimateObject obj;
            /*if (AnimateSkeleton.DRAW_SKELETON) obj = new AnimateSkeleton(body.getMesh().getSkeleton());
            else*/ obj = new ArticulatedMesh(body);
            sceneAsJava3D.addChild(obj.getJava3D());
            behaviour.addObject(obj);
        }
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
    
    private void addAnimation(BranchGroup bg) {
        javax.media.j3d.Alpha alpha = new javax.media.j3d.Alpha(-1, 10000);
        RotationInterpolator interpolator = new RotationInterpolator(alpha, sceneAsJava3D);
        interpolator.setSchedulingBounds(new javax.media.j3d.BoundingSphere(
                new javax.vecmath.Point3d(0.0f, 0.0f, 0.0f), 10));
        Transform3D axis = new Transform3D();
        axis.rotX(-Math.PI / 2.0);
        interpolator.setTransformAxis(axis);
        bg.addChild(interpolator);
    }
    
    private void addLights(BranchGroup bg) {
        // Ambient light
        AmbientLight light1 = new AmbientLight(true, new Color3f(0.25f, 0.25f, 0.25f));
        light1.setInfluencingBounds(new BoundingSphere(new Point3d(0.0f, 0.0f, 0.0f), 10));
        bg.addChild(light1);
        // Directional light
        DirectionalLight light2 = new DirectionalLight();
        light2.setInfluencingBounds(new BoundingSphere(new Point3d(0.0f, 0.0f, 0.0f), 10));
        Vector3f dir = new Vector3f(0.0f, -1.0f, -0.5f);
        dir.normalize();
        light2.setDirection(dir);
        light2.setColor(new Color3f(0.4f, 0.4f, 0.5f));
        bg.addChild(light2);
    }
    
    private void addBackground(BranchGroup bg) {
        // Background colour
        javax.media.j3d.Background backgnd = new javax.media.j3d.Background();
        backgnd.setColor(new Color3f(0.5f, 0.5f, 0.5f));
        backgnd.setApplicationBounds(new javax.media.j3d.BoundingSphere(
                new javax.vecmath.Point3d(0.0f, 0.0f, 0.0f), 10));
        bg.addChild(backgnd);
    }
}

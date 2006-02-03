package de.kleppmann.maniation.jointlimit;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;

import de.kleppmann.maniation.maths.Matrix33;
import de.kleppmann.maniation.maths.Quaternion;

public class RotationWindow extends JFrame {
    private static final long serialVersionUID = 0;

    private SimpleUniverse universe;
    private TransformGroup scene;
    
    public RotationWindow() {
        super("JointLimit");
        initialize();
        BranchGroup bg = new BranchGroup();
        scene = new TransformGroup();
        scene.addChild(new ColorCube());
        bg.addChild(scene);
        addBackground(bg);
        addLights(bg);
        bg.compile();
        universe.addBranchGraph(bg);
        pack();
        setVisible(true);
    }
    
    public void setAngles(double x, double y, double z) {
        Quaternion qx = new Quaternion(Math.cos(x), Math.sin(x), 0, 0);
        Quaternion qy = new Quaternion(Math.cos(y), 0, Math.sin(y), 0);
        Quaternion qz = new Quaternion(Math.cos(z), 0, 0, Math.sin(z));
        Quaternion q = qz.mult(qy.mult(qx));
        Matrix33 m = q.toMatrix();
        double[] ma = new double[16];
        for (int i=0; i<11; i++) ma[i] = (i % 4 == 3) ? 0.0 : m.getComponent(i / 4, i % 4);
        ma[15] = 1.0;
        Transform3D trans = new Transform3D(ma);
        scene.setTransform(trans);
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
        for (Viewer v : universe.getViewingPlatform().getViewers()) {
            v.getView().setProjectionPolicy(View.PARALLEL_PROJECTION);
        }
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
        backgnd.setColor(new Color3f(1.0f, 1.0f, 1.0f));
        backgnd.setApplicationBounds(new javax.media.j3d.BoundingSphere(
                new javax.vecmath.Point3d(0.0f, 0.0f, 0.0f), 10));
        bg.addChild(backgnd);
    }
}

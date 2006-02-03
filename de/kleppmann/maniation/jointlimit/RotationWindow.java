package de.kleppmann.maniation.jointlimit;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;

import de.kleppmann.maniation.maths.Matrix33;
import de.kleppmann.maniation.maths.Quaternion;

public class RotationWindow extends JFrame implements KeyListener {
    private static final long serialVersionUID = 0;

    private SimpleUniverse universe;
    private TransformGroup scene, rotation, mouseView;
    
    public RotationWindow() {
        super("JointLimit");
        addKeyListener(this);
        initialize();
        rotation = new TransformGroup();
        rotation.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        rotation.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        rotation.addChild(new ColorCube());
        mouseView = new TransformGroup();
        mouseView.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        mouseView.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        mouseView.addChild(rotation);
        scene = new TransformGroup();
        Transform3D scale = new Transform3D();
        scale.setScale(0.3);
        scene.setTransform(scale);
        scene.addChild(mouseView);
        BranchGroup bg = new BranchGroup();
        bg.addChild(scene);
        buildCoordinateSystem();
        addMouse();
        addBackground(bg);
        addLights(bg);
        bg.compile();
        universe.addBranchGraph(bg);
        pack();
        setLocation(900, 100);
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
        rotation.setTransform(trans);
    }
    
    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
        mouseView.setTransform(new Transform3D());
    }

    private void initialize() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }            
        });
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas.setPreferredSize(new Dimension(250, 250));
        canvas.addKeyListener(this);
        getContentPane().add(canvas);
        universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();
        for (Viewer v : universe.getViewingPlatform().getViewers()) {
            v.getView().setProjectionPolicy(View.PARALLEL_PROJECTION);
        }
    }
    
    private void buildCoordinateSystem() {
        LineArray axes = new LineArray(6, LineArray.COORDINATES | LineArray.COLOR_3);
        double[] or = {0.0, 0.0, 0.0}, xa = {10.0, 0.0, 0.0}, ya = {0.0, 10.0, 0.0}, za = {0.0, 0.0, 10.0};
        float[] red = {1.0f, 0.0f, 0.0f}, green = {0.0f, 1.0f, 0.0f}, blue = {0.0f, 0.0f, 1.0f};
        axes.setCoordinate(0, or); axes.setColor(0, red);
        axes.setCoordinate(1, xa); axes.setColor(1, red);
        axes.setCoordinate(2, or); axes.setColor(2, green);
        axes.setCoordinate(3, ya); axes.setColor(3, green);
        axes.setCoordinate(4, or); axes.setColor(4, blue);
        axes.setCoordinate(5, za); axes.setColor(5, blue);
        Appearance axisapp = new Appearance();
        axisapp.setLineAttributes(new LineAttributes(2, LineAttributes.PATTERN_SOLID, true));
        Shape3D shape = new Shape3D(axes, axisapp);
        mouseView.addChild(shape);
    }
    
    private void addMouse() {
        MouseRotate rotate = new MouseRotate();
        rotate.setSchedulingBounds(new BoundingSphere(new Point3d(0.0f, 0.0f, 0.0f), 10));
        rotate.setTransformGroup(mouseView);
        rotate.setFactor(0.003);
        mouseView.addChild(rotate);
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

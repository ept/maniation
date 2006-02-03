package de.kleppmann.maniation.jointlimit;

import java.awt.Dimension;
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
import javax.media.j3d.Material;
import javax.media.j3d.PointArray;
import javax.media.j3d.PointAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;


public class DisplayWindow extends JFrame {
    private static final long serialVersionUID = 0;
    
    private static final float[] UNSELECTED = {0f, 0f, 0f}, SELECTED = {1f, 0f, 0f};

    private Canvas3D canvas;
    private SimpleUniverse universe;
    private TransformGroup scene, mouseTransform;
    private BranchGroup bg;
    private PointArray points;
    private int selected = -1;
    
    public DisplayWindow(double[][] contents, double boxsize) {
        super("JointLimit");
        initialize();
        if (false) buildSolid(contents, boxsize);
        buildWireframe(contents, boxsize);
        buildCoordinateSystem();
        addBackground();
        addLights();
        addMouse();
        bg.addChild(new PointPicker(this));
        bg.compile();
        universe.addBranchGraph(bg);
        pack();
        setVisible(true);
    }
    
    public BranchGroup getBranchGroup() {
        return bg;
    }
    
    public Canvas3D getCanvas3D() {
        return canvas;
    }
    
    public void setSelectedPoint(int index) {
        if (selected >= 0) points.setColor(selected, UNSELECTED);
        selected = index;
        float[] col = new float[3];
        points.getColor(selected, col);
        points.setColor(selected, SELECTED);
        for (float f : col) System.out.print(f + "  ");
        System.out.println();
    }
    
    private void initialize() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }            
        });
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas.setPreferredSize(new Dimension(800, 600));
        getContentPane().add(canvas);
        universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();
        for (Viewer v : universe.getViewingPlatform().getViewers()) {
            v.getView().setProjectionPolicy(View.PARALLEL_PROJECTION);
        }
        bg = new BranchGroup();
        scene = new TransformGroup();
        Transform3D scale = new Transform3D();
        scale.setScale(0.3);
        scene.setTransform(scale);
        mouseTransform = new TransformGroup();
        mouseTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        mouseTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        mouseTransform.addChild(scene);
        bg.addChild(mouseTransform);
    }
    
    private void buildCoordinateSystem() {
        int count = 6; double dist = 0.2;
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
        Shape3D shape1 = new Shape3D(axes, axisapp);
        shape1.setPickable(false);
        scene.addChild(shape1);
        LineArray lines = new LineArray(count*12, LineArray.COORDINATES | LineArray.COLOR_3);
        int i = 0;
        for (int x=1; x<=count; x++) {
            double[] x1 = {x*dist, 10f, 0f}, x2 = {x*dist, 0f, 0f}, x3 = {x*dist, 0f, 10f};
            lines.setCoordinate(i, x1); lines.setColor(i, green); i++;
            lines.setCoordinate(i, x2); lines.setColor(i, green); i++;
            lines.setCoordinate(i, x2); lines.setColor(i, blue); i++;
            lines.setCoordinate(i, x3); lines.setColor(i, blue); i++;
        }
        for (int y=1; y<=count; y++) {
            double[] x1 = {10f, y*dist, 0f}, x2 = {0f, y*dist, 0f}, x3 = {0f, y*dist, 10f};
            lines.setCoordinate(i, x1); lines.setColor(i, red); i++;
            lines.setCoordinate(i, x2); lines.setColor(i, red); i++;
            lines.setCoordinate(i, x2); lines.setColor(i, blue); i++;
            lines.setCoordinate(i, x3); lines.setColor(i, blue); i++;
        }
        for (int z=1; z<=count; z++) {
            double[] x1 = {10f, 0f, z*dist}, x2 = {0f, 0f, z*dist}, x3 = {0f, 10f, z*dist};
            lines.setCoordinate(i, x1); lines.setColor(i, red); i++;
            lines.setCoordinate(i, x2); lines.setColor(i, red); i++;
            lines.setCoordinate(i, x2); lines.setColor(i, green); i++;
            lines.setCoordinate(i, x3); lines.setColor(i, green); i++;
        }
        Appearance lineapp = new Appearance();
        lineapp.setLineAttributes(new LineAttributes(1, LineAttributes.PATTERN_SOLID, true));
        Shape3D shape2 = new Shape3D(lines, lineapp);
        shape2.setPickable(false);
        scene.addChild(shape2);
    }
    
    private void buildWireframe(double[][] contents, double boxsize) {
        points = new PointArray(contents.length, PointArray.COORDINATES | PointArray.COLOR_3);
        points.setCapability(PointArray.ALLOW_COUNT_READ);
        points.setCapability(PointArray.ALLOW_FORMAT_READ);
        points.setCapability(PointArray.ALLOW_COORDINATE_READ);
        points.setCapability(PointArray.ALLOW_COLOR_READ);
        points.setCapability(PointArray.ALLOW_COLOR_WRITE);
        for (int i=0; i<contents.length; i++) {
            points.setCoordinate(i, contents[i]);
            if (i == selected) points.setColor(i, SELECTED); else points.setColor(i, UNSELECTED);
        }
        Appearance appearance = new Appearance();
        //appearance.setColoringAttributes(new ColoringAttributes(0f, 0f, 0f, ColoringAttributes.SHADE_FLAT));
        appearance.setPointAttributes(new PointAttributes(4, true));
        Shape3D shape = new Shape3D(points, appearance);
        shape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        scene.addChild(shape);
    }
    
    private void buildSolid(double[][] contents, double boxsize) {
        IndexedQuadArray geometry = new IndexedQuadArray(8*contents.length,
                IndexedQuadArray.COORDINATES | IndexedQuadArray.NORMALS, 24*contents.length);
        float s = (float) Math.sqrt(2) / 2.0f;
        float[] n0 = {-s, -s, -s}, n1 = {-s, -s, +s}, n2 = {-s, +s, -s}, n3 = {-s, +s, +s},
                n4 = {+s, -s, -s}, n5 = {+s, -s, +s}, n6 = {+s, +s, -s}, n7 = {+s, +s, +s};
        for (int i=0; i<contents.length; i++) {
            double[] a0 = {contents[i][0] - boxsize/2.0, contents[i][1] - boxsize/2.0, contents[i][2] - boxsize/2.0};
            double[] a1 = {contents[i][0] - boxsize/2.0, contents[i][1] - boxsize/2.0, contents[i][2] + boxsize/2.0};
            double[] a2 = {contents[i][0] - boxsize/2.0, contents[i][1] + boxsize/2.0, contents[i][2] - boxsize/2.0};
            double[] a3 = {contents[i][0] - boxsize/2.0, contents[i][1] + boxsize/2.0, contents[i][2] + boxsize/2.0};
            double[] a4 = {contents[i][0] + boxsize/2.0, contents[i][1] - boxsize/2.0, contents[i][2] - boxsize/2.0};
            double[] a5 = {contents[i][0] + boxsize/2.0, contents[i][1] - boxsize/2.0, contents[i][2] + boxsize/2.0};
            double[] a6 = {contents[i][0] + boxsize/2.0, contents[i][1] + boxsize/2.0, contents[i][2] - boxsize/2.0};
            double[] a7 = {contents[i][0] + boxsize/2.0, contents[i][1] + boxsize/2.0, contents[i][2] + boxsize/2.0};
            geometry.setCoordinate(8*i+0, a0); geometry.setNormal(8*i+0, n0);
            geometry.setCoordinate(8*i+1, a1); geometry.setNormal(8*i+1, n1);
            geometry.setCoordinate(8*i+2, a2); geometry.setNormal(8*i+2, n2);
            geometry.setCoordinate(8*i+3, a3); geometry.setNormal(8*i+3, n3);
            geometry.setCoordinate(8*i+4, a4); geometry.setNormal(8*i+4, n4);
            geometry.setCoordinate(8*i+5, a5); geometry.setNormal(8*i+5, n5);
            geometry.setCoordinate(8*i+6, a6); geometry.setNormal(8*i+6, n6);
            geometry.setCoordinate(8*i+7, a7); geometry.setNormal(8*i+7, n7);
            geometry.setCoordinateIndex(24*i+ 0, 8*i+0);
            geometry.setCoordinateIndex(24*i+ 1, 8*i+1);
            geometry.setCoordinateIndex(24*i+ 2, 8*i+5);
            geometry.setCoordinateIndex(24*i+ 3, 8*i+4);
            geometry.setCoordinateIndex(24*i+ 4, 8*i+0);
            geometry.setCoordinateIndex(24*i+ 5, 8*i+4);
            geometry.setCoordinateIndex(24*i+ 6, 8*i+6);
            geometry.setCoordinateIndex(24*i+ 7, 8*i+2);
            geometry.setCoordinateIndex(24*i+ 8, 8*i+0);
            geometry.setCoordinateIndex(24*i+ 9, 8*i+2);
            geometry.setCoordinateIndex(24*i+10, 8*i+3);
            geometry.setCoordinateIndex(24*i+11, 8*i+1);
            geometry.setCoordinateIndex(24*i+12, 8*i+1);
            geometry.setCoordinateIndex(24*i+13, 8*i+3);
            geometry.setCoordinateIndex(24*i+14, 8*i+7);
            geometry.setCoordinateIndex(24*i+15, 8*i+5);
            geometry.setCoordinateIndex(24*i+16, 8*i+5);
            geometry.setCoordinateIndex(24*i+17, 8*i+7);
            geometry.setCoordinateIndex(24*i+18, 8*i+6);
            geometry.setCoordinateIndex(24*i+19, 8*i+4);
            geometry.setCoordinateIndex(24*i+20, 8*i+2);
            geometry.setCoordinateIndex(24*i+21, 8*i+6);
            geometry.setCoordinateIndex(24*i+22, 8*i+7);
            geometry.setCoordinateIndex(24*i+23, 8*i+3);
        }
        Appearance appearance = new Appearance();
        appearance.setMaterial(new Material(new Color3f(1f, 1f, 1f), new Color3f(0f, 0f, 0f),
                new Color3f(1f, 1f, 1f), new Color3f(1f, 1f, 1f), 1f));
        scene.addChild(new Shape3D(geometry, appearance));
    }
    
    private void addMouse() {
        MouseRotate rotate = new MouseRotate();
        rotate.setSchedulingBounds(new BoundingSphere(new Point3d(0.0f, 0.0f, 0.0f), 10));
        rotate.setTransformGroup(mouseTransform);
        rotate.setFactor(0.003);
        mouseTransform.addChild(rotate);
    }
    
    private void addLights() {
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
    
    private void addBackground() {
        // Background colour
        javax.media.j3d.Background backgnd = new javax.media.j3d.Background();
        backgnd.setColor(new Color3f(1.0f, 1.0f, 1.0f));
        backgnd.setApplicationBounds(new javax.media.j3d.BoundingSphere(
                new javax.vecmath.Point3d(0.0f, 0.0f, 0.0f), 10));
        bg.addChild(backgnd);
    }
}

package de.kleppmann.maniation.java3d_test;

import java.awt.Dimension;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;
import javax.swing.JFrame;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Test extends JFrame {
    
    private static final long serialVersionUID = 0;
    
    public Test() {
        super("Java3D test");
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas.setPreferredSize(new Dimension(800, 600));
        getContentPane().add(canvas);
        SimpleUniverse universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();
        
        GeometryArray geometry = new TriangleArray(3, TriangleArray.COORDINATES |
                TriangleArray.COLOR_3);
        double[] a = {0.0d, 0.0d, 0.0d};
        double[] b = {0.3d, 0.0d, 0.0d};
        double[] c = {0.3d, 0.3d, 0.0d};
        geometry.setCoordinate(0, a);
        geometry.setCoordinate(1, b);
        geometry.setCoordinate(2, c);
        float[] col = {1.0f, 1.0f, 0.0f};
        geometry.setColor(0, col);
        geometry.setColor(1, col);
        geometry.setColor(2, col);
        Shape3D shape = new Shape3D();
        shape.setGeometry(geometry);
        //Transform3D rotX = new Transform3D();
        //Transform3D rotY = new Transform3D();
        //rotX.rotX(Math.PI/4.0);
        //rotY.rotY(Math.PI/5.0);
        //rotX.mul(rotY);
        //TransformGroup cubeTransform = new TransformGroup(rotX);
        //cubeTransform.addChild(shape);
        BranchGroup cube = new BranchGroup();
        cube.addChild(shape);
        cube.compile();
        
        universe.addBranchGraph(cube);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        new Test();
    }
}

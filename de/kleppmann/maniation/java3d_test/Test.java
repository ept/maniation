package de.kleppmann.maniation.java3d_test;

import java.awt.Dimension;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import com.sun.j3d.utils.geometry.ColorCube;
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
        
        BranchGroup cube = new BranchGroup();
        ColorCube cubeMesh = new ColorCube(0.4);
        Transform3D rotX = new Transform3D();
        Transform3D rotY = new Transform3D();
        rotX.rotX(Math.PI/4.0);
        rotY.rotY(Math.PI/5.0);
        rotX.mul(rotY);
        TransformGroup cubeTransform = new TransformGroup(rotX);
        cube.addChild(cubeTransform);
        cubeTransform.addChild(cubeMesh);
        cube.compile();
        
        universe.addBranchGraph(cube);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        new Test();
    }
}

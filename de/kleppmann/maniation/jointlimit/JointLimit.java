package de.kleppmann.maniation.jointlimit;

import java.util.List;

import de.kleppmann.maniation.maths.Vector3D;

public class JointLimit {
    
    public static final int RESOLUTION = 100;
    public static final double MIN_X = -1.8;
    public static final double MAX_X =  1.8;
    public static final double MIN_Y = -1.8;
    public static final double MAX_Y =  1.8;
    public static final double MIN_Z = -1.8;
    public static final double MAX_Z =  1.8;
    
    private Inequalities scene;
    
    public JointLimit() {
        Inequality sphere = new Inequality() {
            public double getValue(double x, double y, double z) {
                return 1.0 - x*x - y*y - z*z;
            }
        };
        scene = new Inequalities();
        scene.getFunctions().add(sphere);
    }
    
    public double[][] build() {
        List<Vector3D> boxCentres = new java.util.ArrayList<Vector3D>();
        boolean[][][] sat = new boolean[RESOLUTION][RESOLUTION][3];
        for (int zi=0; zi<RESOLUTION; zi++) {
            double z = (MAX_Z - MIN_Z)*zi/(RESOLUTION - 1.0) + MIN_Z;
            double zp = (MAX_Z - MIN_Z)*(zi-1.0)/(RESOLUTION - 1.0) + MIN_Z;
            for (int yi=0; yi<RESOLUTION; yi++) {
                double y = (MAX_Y - MIN_Y)*yi/(RESOLUTION - 1.0) + MIN_Y;
                for (int xi=0; xi<RESOLUTION; xi++) {
                    double x = (MAX_X - MIN_X)*xi/(RESOLUTION - 1.0) + MIN_X;
                    sat[xi][yi][2] = scene.satisfiedAt(x, y, z);
                    boolean edge = (zi == 0) || (zi == RESOLUTION - 1) ||
                        (yi == 0) || (yi == RESOLUTION - 1) ||
                        (xi == 0) || (xi == RESOLUTION - 1);
                    boolean enclosed = false;
                    if (!edge) {
                        enclosed = sat[xi][yi][0] && sat[xi][yi][2] &&
                            sat[xi-1][yi][1] && sat[xi+1][yi][1] &&
                            sat[xi][yi-1][1] && sat[xi][yi+1][1];
                    }
                    if (sat[xi][yi][1] && (edge || !enclosed))
                        boxCentres.add(new Vector3D(x, y, zp));
                }
            }
            for (int yi=0; yi<RESOLUTION; yi++)
                for (int xi=0; xi<RESOLUTION; xi++) {
                    sat[xi][yi][0] = sat[xi][yi][1];
                    sat[xi][yi][1] = sat[xi][yi][2];
                }
        }
        double[][] result = new double[boxCentres.size()][3];
        for (int i=0; i<result.length; i++) {
            for (int j=0; j<3; j++) result[i][j] = boxCentres.get(i).getComponent(j);
        }
        return result;
    }

    public static void main(String[] args) {
        new DisplayWindow((new JointLimit()).build(), (MAX_X - MIN_X)/(RESOLUTION - 1.0));
    }
}

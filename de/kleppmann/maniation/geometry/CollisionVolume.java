package de.kleppmann.maniation.geometry;


public class CollisionVolume {
    
    private double[] coordinates;
    private double[] centres;
    private int[] indices;
    private TreeNode tree;

    public CollisionVolume(double[] coordinates, int[] indices) {
        this.coordinates = coordinates;
        this.indices = indices;
        this.centres = new double[indices.length];
        calcCentres();
        buildMaxVolume();
        for (int i=0; i<centres.length/3; i++) 
            tree.add(centres[3*i], centres[3*i+1], centres[3*i+2], i);
    }
    
    private void calcCentres() {
        for (int i=0; i<indices.length/3; i++) {
            for (int j=0; j<3; j++) {
                centres[3*i+j] = (coordinates[3*indices[3*i+0]+j] +
                                  coordinates[3*indices[3*i+1]+j] +
                                  coordinates[3*indices[3*i+2]+j]) / 3.0;
            }
        }
    }
    
    private void buildMaxVolume() {
        // FIXME need to calculate bounding box based on coordinates, not on centres!
        double[] max = new double[3], min = new double[3];
        for (int i=0; i<3; i++) max[i] = min[i] = centres[i];
        for (int i=3; i<centres.length; i++) {
            if (centres[i] < min[i%3]) min[i%3] = centres[i];
            if (centres[i] > max[i%3]) max[i%3] = centres[i];
        }
        tree = new TreeNode(max[0], min[0], max[1], min[1], max[2], min[2]);
    }

    
    private enum Subdivision { X, Y, Z };
    
    private class TreeNode {
        Subdivision div;
        TreeNode lower, upper;
        double maxx, minx, maxy, miny, maxz, minz;
        int triangles, triangleIndex;
        
        TreeNode(double maxx, double minx, double maxy, double miny,
                double maxz, double minz) {
            this.maxx = maxx; this.minx = minx;
            this.maxy = maxy; this.miny = miny;
            this.maxz = maxz; this.minz = minz;
            this.triangles = 0;
        }
        
        void add(double x, double y, double z, int index) {
            triangles++;
            if (triangles == 1) {
                triangleIndex = index; return;
            }
            if (triangles == 2) subdivide();
            switch (div) {
            case X:
                if (x >= 0.5*(minx + maxx)) upper.add(x, y, z, index);
                else lower.add(x, y, z, index);
                break;
            case Y:
                if (y >= 0.5*(miny + maxy)) upper.add(x, y, z, index);
                else lower.add(x, y, z, index);
                break;
            case Z:
                if (z >= 0.5*(minz + maxz)) upper.add(x, y, z, index);
                else lower.add(x, y, z, index);
                break;
            }
        }
        
        private void subdivide() {
            double dx = maxx - minx, dy = maxy - miny, dz = maxz - minz;
            if ((dx >= dy) && (dx >= dz)) {
                div = Subdivision.X;
                upper = new TreeNode(maxx, 0.5*(minx + maxx), maxy, miny, maxz, minz);
                lower = new TreeNode(0.5*(minx + maxx), minx, maxy, miny, maxz, minz);
            } else if (dy >= dz) {
                div = Subdivision.Y;
                upper = new TreeNode(maxx, minx, maxy, 0.5*(miny + maxy), maxz, minz);
                lower = new TreeNode(maxx, minx, 0.5*(miny + maxy), miny, maxz, minz);
            } else {
                div = Subdivision.Z;
                upper = new TreeNode(maxx, minx, maxy, miny, maxz, 0.5*(minz + maxz));
                lower = new TreeNode(maxx, minx, maxy, miny, 0.5*(minz + maxz), minz);
            }
        }
    }
}

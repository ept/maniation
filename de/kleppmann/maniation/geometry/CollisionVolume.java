package de.kleppmann.maniation.geometry;

import de.kleppmann.maniation.maths.Vector3D;


/**
 * Using Axis-Aligned Bounding Boxes (AABBs).
 */
public class CollisionVolume {
    
    private Tree tree;
    private MeshTriangle[] triangles;
    private int tests;

    public CollisionVolume(MeshTriangle[] triangles) {
        this.triangles = triangles;
        tree = newTree(triangles);
    }
    
    public MeshTriangle[] getTriangles() {
        return triangles;
    }
    
    public void updateBBox() {
        tree.updateBBox();
    }
    
    public BoundingBox getBBox() {
        return tree.getBBox();
    }
    
    public double size() {
        double vx = tree.getBBox().maxx - tree.getBBox().minx;
        double vy = tree.getBBox().maxy - tree.getBBox().miny;
        double vz = tree.getBBox().maxz - tree.getBBox().minz;
        return Math.sqrt(vx*vx + vy*vy + vz*vz);
    }
    
    public void intersect(CollisionVolume other, Collision result) {
        tests = 0;
        tree.intersect(other.tree, result);
        /*if (tests > 0) System.out.println(tests + " primitive tests out of " +
                getTriangles().length*other.getTriangles().length);*/
    }
    
    public int intersections(Vector3D p1, Vector3D p2) {
        return tree.intersections(p1, p2);
    }
    
    private Tree newTree(MeshTriangle[] triangles) {
        if (triangles.length == 0) return new EmptyTree();
        if (triangles.length == 1) return new TreeLeaf(triangles[0]);
        return new TreeNode(triangles);
    }
    
    
    private interface Tree {
        BoundingBox getBBox();
        void updateBBox();
        void intersect(Tree other, Collision result);
        int intersections(Vector3D p1, Vector3D p2);
    }
    
    
    private class EmptyTree implements Tree {
        BoundingBox bbox = new BoundingBox();
        public BoundingBox getBBox() { return bbox; }
        public void updateBBox() {}
        public void intersect(Tree other, Collision result) {}
        public int intersections(Vector3D p1, Vector3D p2) { return 0; }
    }
    
    
    private class TreeLeaf implements Tree {
        MeshTriangle triangle;
        
        TreeLeaf(MeshTriangle triangle) {
            this.triangle = triangle;
        }
        
        public BoundingBox getBBox() {
            return triangle.bbox;
        }
        
        public void updateBBox() {
            triangle.updateBBox();
        }
        
        public void intersect(Tree other, Collision result) {
            if (other instanceof TreeLeaf) {
                triangle.intersect(((TreeLeaf) other).triangle, result);
                tests++;
            } else other.intersect(this, result);
        }
        
        public int intersections(Vector3D p1, Vector3D p2) {
            if (triangle.lineAgainstTriangle(p1.getComponent(0), p1.getComponent(1), p1.getComponent(2),
                    p2.getComponent(0), p2.getComponent(1), p2.getComponent(2)) != null) return 1;
            return 0;
        }
    }
    
    
    private class TreeNode implements Tree {
        Tree lower, upper;
        BoundingBox bbox;
        
        TreeNode(MeshTriangle[] triangles) {
            if (triangles.length < 2) throw new IllegalArgumentException();
            bbox = new BoundingBox(triangles);
            subdivide(triangles);
        }
        
        private void subdivide(MeshTriangle[] triangles) {
            double dx = bbox.maxx - bbox.minx;
            double dy = bbox.maxy - bbox.miny;
            double dz = bbox.maxz - bbox.minz;
            MeshVertex.Component subdivision;
            if ((dx >= dy) && (dx >= dz)) subdivision = MeshVertex.Component.X;
                else if (dy >= dz) subdivision = MeshVertex.Component .Y;
                else subdivision = MeshVertex.Component .Z;
            double midpoint = 0.0;
            for (MeshTriangle tri : triangles) midpoint += tri.centre[subdivision.offset];
            midpoint /= triangles.length;
            int count = 0;
            for (MeshTriangle tri : triangles) if (tri.centre[subdivision.offset] < midpoint) count++;
            MeshTriangle[] lowerSet, upperSet;
            if ((count == 0) || (count == triangles.length)) {
                // Special handling to avoid infinite looping in pathological cases
                count = triangles.length / 2;
                lowerSet = new MeshTriangle[count];
                upperSet = new MeshTriangle[triangles.length - count];
                for (int j=0; j<triangles.length; j++)
                    if (j < count) lowerSet[j] = triangles[j]; else upperSet[j-count] = triangles[j];
            } else {
                // Normal case
                lowerSet = new MeshTriangle[count];
                upperSet = new MeshTriangle[triangles.length - count];
                int j = 0, k = 0;
                for (MeshTriangle tri : triangles)
                    if (tri.centre[subdivision.offset] < midpoint) {
                        lowerSet[j] = tri; j++;
                    } else {
                        upperSet[k] = tri; k++;
                    }
            }
            upper = newTree(upperSet);
            lower = newTree(lowerSet);
        }
        
        public BoundingBox getBBox() {
            return bbox;
        }
        
        public void updateBBox() {
            upper.updateBBox();
            lower.updateBBox();
            bbox = new BoundingBox(upper.getBBox(), lower.getBBox());
        }
        
        public void intersect(Tree other, Collision result) {
            if (!bbox.intersects(other.getBBox())) return;
            if (other instanceof TreeNode) {
                ((TreeNode) other).lower.intersect(this.lower, result);
                ((TreeNode) other).lower.intersect(this.upper, result);
                ((TreeNode) other).upper.intersect(this.lower, result);
                ((TreeNode) other).upper.intersect(this.upper, result);
            } else {
                this.lower.intersect(other, result);
                this.upper.intersect(other, result);
            }
        }

        public int intersections(Vector3D p1, Vector3D p2) {
            if (!bbox.intersects(p1, p2)) return 0;
            return upper.intersections(p1, p2) + lower.intersections(p1, p2);
        }
    }
}

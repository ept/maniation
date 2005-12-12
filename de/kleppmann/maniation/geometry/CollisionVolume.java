package de.kleppmann.maniation.geometry;


/**
 * Using Axis-Aligned Bounding Boxes (AABBs).
 */
public class CollisionVolume {
    
    private Tree tree;
    //private int triangleCount;
    private int tests;

    public CollisionVolume(MeshTriangle[] triangles) {
        //triangleCount = triangles.length;
        tree = newTree(triangles);
    }
    
    public void updateBBox() {
        tree.updateBBox();
    }
    
    public void intersect(CollisionVolume other, Collision result) {
        tests = 0;
        tree.intersect(other.tree, result);
        //System.out.println(tests + " primitive tests out of " + triangleCount*other.triangleCount);
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
    }
    
    
    private class EmptyTree implements Tree {
        BoundingBox bbox = new BoundingBox();
        public BoundingBox getBBox() { return bbox; }
        public void updateBBox() {}
        public void intersect(Tree other, Collision result) {}
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
    }
}

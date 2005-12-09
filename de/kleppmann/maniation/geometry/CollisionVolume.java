package de.kleppmann.maniation.geometry;


/**
 * Using Axis-Aligned Bounding Boxes (AABBs).
 */
public class CollisionVolume {
    
    private MeshTriangle[] triangles;
    private TreeNode tree;


    public CollisionVolume(MeshTriangle[] triangles) {
        this.triangles = triangles;
        tree = new TreeNode(triangles);
    }
    
    
    private class TreeNode {
        MeshVertex.Component subdivision;
        TreeNode lower, upper;
        BoundingBox bbox;
        MeshTriangle[] triangles;
        
        TreeNode(MeshTriangle[] triangles) {
            this.triangles = triangles;
            if (triangles.length == 0) {
                bbox = new BoundingBox();
                return;
            }
            if (triangles.length == 1) {
                bbox = triangles[0].bbox;
                return;
            }
            bbox = new BoundingBox(triangles);
            subdivide();
        }
        
        private void subdivide() {
            double dx = bbox.maxx - bbox.minx;
            double dy = bbox.maxy - bbox.miny;
            double dz = bbox.maxz - bbox.minz;
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
            upper = new TreeNode(upperSet);
            lower = new TreeNode(lowerSet);
        }
    }
}

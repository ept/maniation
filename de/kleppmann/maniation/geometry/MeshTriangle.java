package de.kleppmann.maniation.geometry;


/**
 * A triangle in a mesh in which the vertex positions may change, but
 * the triangle configurations stay constant.
 */
public class MeshTriangle {
    
    MeshVertex[] vertices;
    BoundingBox bbox;
    double[] centre;

    public MeshTriangle(MeshVertex v1, MeshVertex v2, MeshVertex v3) {
        vertices = new MeshVertex[3];
        vertices[0] = v1;
        vertices[1] = v2;
        vertices[2] = v3;
        centre = new double[3];
        updateBBox();
    }
    
    public void updateBBox() {
        bbox = new BoundingBox(
            vertices[0].max3(vertices[1], vertices[2], MeshVertex.Component.X),
            vertices[0].min3(vertices[1], vertices[2], MeshVertex.Component.X),
            vertices[0].max3(vertices[1], vertices[2], MeshVertex.Component.Y),
            vertices[0].min3(vertices[1], vertices[2], MeshVertex.Component.Y),
            vertices[0].max3(vertices[1], vertices[2], MeshVertex.Component.Z),
            vertices[0].min3(vertices[1], vertices[2], MeshVertex.Component.Z));
        centre[0] = vertices[0].avg3(vertices[1], vertices[2], MeshVertex.Component.X);
        centre[1] = vertices[0].avg3(vertices[1], vertices[2], MeshVertex.Component.Y);
        centre[2] = vertices[0].avg3(vertices[1], vertices[2], MeshVertex.Component.Z);
    }

}

package de.kleppmann.maniation.geometry;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GeometryUpdater;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.Shape3D;

import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.scene.Mesh;
import de.kleppmann.maniation.scene.Vertex;

public class MeshDeformation implements AnimateObject, GeometryUpdater {
    
    private Mesh mesh;
    private double[] coordinates;
    private float[] normals;
    private GeometryArray geometry;
    private Shape3D shape;
    private AnimateSkeleton skeleton;

    public MeshDeformation(Mesh mesh) {
        this.mesh = mesh;
        this.skeleton = new AnimateSkeleton(mesh.getSkeleton());
        buildArrays();
        buildJava3D();
    }
    
    private void buildArrays() {
        coordinates = new double[3*mesh.getVertices().size()];
        normals = new float[3*mesh.getVertices().size()];
        int i = 0;
        for (Vertex v : mesh.getVertices()) {
            coordinates[3*i+0] = v.getPosition().getX();
            coordinates[3*i+1] = v.getPosition().getY();
            coordinates[3*i+2] = v.getPosition().getZ();
            normals[3*i+0] = (float) v.getNormal().getX();
            normals[3*i+1] = (float) v.getNormal().getY();
            normals[3*i+2] = (float) v.getNormal().getZ();
            i++;
        }
    }
    
    private void buildJava3D() {
        IndexedTriangleArray triangles = new IndexedTriangleArray(mesh.getVertices().size(),
                IndexedTriangleArray.COORDINATES |
                IndexedTriangleArray.NORMALS |
                IndexedTriangleArray.BY_REFERENCE |
                IndexedTriangleArray.USE_COORD_INDEX_ONLY,
                3*mesh.getFaces().size());
        triangles.setCapability(IndexedTriangleArray.ALLOW_REF_DATA_READ);
        triangles.setCapability(IndexedTriangleArray.ALLOW_REF_DATA_WRITE);
        triangles.setCapability(IndexedTriangleArray.ALLOW_COUNT_READ);
        triangles.setCoordRefDouble(coordinates);
        triangles.setNormalRefFloat(normals);
        for (int i=0; i<mesh.getFaces().size(); i++) {
            for (int j=0; j<3; j++) {
                int index = mesh.getVertices().indexOf(
                        mesh.getFaces().get(i).getVertices().get(j));
                triangles.setCoordinateIndex(3*i+j, index);
            }
        }
        geometry = triangles;
        Appearance appearance = new Appearance();
        appearance.setMaterial(mesh.getMaterial().getJava3D());
        shape = new Shape3D(geometry, appearance);
    }

    public Shape3D getShape3D() {
        return shape;
    }
    
    public void processStimulus() {
        geometry.updateData(this);
    }
    
    public void updateData(Geometry geometry) {
        skeleton.updateData(geometry);
        int coordIndex = 0;
        for (Vertex vert : mesh.getVertices()) {
            Vector deformed = skeleton.currentVertexPosition(vert);
            deformed.toDoubleArray(coordinates, 3*coordIndex);
            coordIndex++;
        }
    }
}

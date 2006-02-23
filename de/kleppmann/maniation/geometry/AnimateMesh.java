package de.kleppmann.maniation.geometry;

import java.util.Map;

import javax.media.j3d.Appearance;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

import de.kleppmann.maniation.dynamics.Body;
import de.kleppmann.maniation.maths.Matrix33;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.scene.Face;
import de.kleppmann.maniation.scene.Mesh;
import de.kleppmann.maniation.scene.Vertex;

public class AnimateMesh implements AnimateObject {

    Mesh mesh;
    Body body;
    double[] coordinates;
    float[] normals;
    MeshVertex[] vertices;
    MeshTriangle[] triangles;
    Map<Vertex, MeshVertex> vertexMap;
    IndexedTriangleArray geometry;
    TransformGroup trans;

    public AnimateMesh(Mesh mesh, Body body) {
        this.mesh = mesh;
        buildArrays();
        buildJava3D();
    }
    
    private Transform3D currentTransform() {
        Transform3D result = new Transform3D();
        Vector3D pos = body.getLocation();
        result.setTranslation(new Vector3d(pos.getComponent(0), pos.getComponent(1),
                pos.getComponent(2)));
        Matrix33 rot = body.getOrientation().toMatrix();
        double[] rotm = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1};
        for (int i=0; i<12; i++)
            if (i % 4 < 3) rotm[i] = rot.getComponent(i / 4, i % 4);
        result.mul(new Transform3D(rotm));
        return result;
    }
    
    private void buildArrays() {
        coordinates = new double[3*mesh.getVertices().size()];
        normals = new float[3*mesh.getVertices().size()];
        vertices = new MeshVertex[mesh.getVertices().size()];
        vertexMap = new java.util.HashMap<Vertex, MeshVertex>();
        int i = 0;
        for (Vertex v : mesh.getVertices()) {
            coordinates[3*i+0] = v.getPosition().getX();
            coordinates[3*i+1] = v.getPosition().getY();
            coordinates[3*i+2] = v.getPosition().getZ();
            normals[3*i+0] = (float) v.getNormal().getX();
            normals[3*i+1] = (float) v.getNormal().getY();
            normals[3*i+2] = (float) v.getNormal().getZ();
            vertices[i] = new MeshVertex(coordinates, i);
            vertexMap.put(v, vertices[i]);
            i++;
        }
        triangles = new MeshTriangle[mesh.getFaces().size()];
        i = 0;
        for (Face face : mesh.getFaces()) {
            triangles[i] = new MeshTriangle(
                    vertexMap.get(face.getVertices().get(0)),
                    vertexMap.get(face.getVertices().get(1)),
                    vertexMap.get(face.getVertices().get(2)));
            i++;
        }
    }
    
    private void buildJava3D() {
        geometry = new IndexedTriangleArray(mesh.getVertices().size(),
                IndexedTriangleArray.COORDINATES |
                IndexedTriangleArray.NORMALS |
                IndexedTriangleArray.BY_REFERENCE |
                IndexedTriangleArray.USE_COORD_INDEX_ONLY,
                3*mesh.getFaces().size());
        geometry.setCapability(IndexedTriangleArray.ALLOW_REF_DATA_READ);
        geometry.setCapability(IndexedTriangleArray.ALLOW_REF_DATA_WRITE);
        geometry.setCapability(IndexedTriangleArray.ALLOW_COUNT_READ);
        geometry.setCoordRefDouble(coordinates);
        geometry.setNormalRefFloat(normals);
        for (int i=0; i<triangles.length; i++) {
            MeshTriangle tri = triangles[i];
            for (int j=0; j<3; j++)
                geometry.setCoordinateIndex(3*i+j, tri.vertices[j].index);
        }
        Appearance appearance = new Appearance();
        appearance.setMaterial(mesh.getMaterial().getJava3D());
        Shape3D shape = new Shape3D(geometry, appearance);
        trans = new TransformGroup(currentTransform());
        trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans.addChild(shape);
    }


    public void processStimulus() {
        trans.setTransform(currentTransform());
    }

    public Node getJava3D() {
        return trans;
    }
}

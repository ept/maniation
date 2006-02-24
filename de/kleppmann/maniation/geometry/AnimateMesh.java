package de.kleppmann.maniation.geometry;

import java.util.Map;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryUpdater;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;

import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.scene.Body;
import de.kleppmann.maniation.scene.Face;
import de.kleppmann.maniation.scene.Mesh;
import de.kleppmann.maniation.scene.Vertex;

public class AnimateMesh implements AnimateObject {

    Body sceneBody;
    double[] coordinates;
    float[] normals;
    MeshVertex[] vertices;
    MeshTriangle[] triangles;
    Map<Vertex, MeshVertex> vertexMap;
    IndexedTriangleArray geometry;
    private Shape3D shape;
    private MyUpdater myUpdater = new MyUpdater();
    private CollisionVolume volume;

    public AnimateMesh(Body sceneBody) {
        this.sceneBody = sceneBody;
        buildArrays();
        buildJava3D();
        volume = new CollisionVolume(triangles);
    }
    
    public Body getSceneBody() {
        return sceneBody;
    }
    
    public CollisionVolume getCollisionVolume() {
        return volume;
    }
    
    public Vector3D getLocation() {
        return new Vector3D(sceneBody.getLocation().getX(), 
                sceneBody.getLocation().getY(), sceneBody.getLocation().getZ());
    }
    
    public void setLocation(Vector3D location) {
        sceneBody.getLocation().setX(location.getComponent(0));
        sceneBody.getLocation().setY(location.getComponent(1));
        sceneBody.getLocation().setZ(location.getComponent(2));
    }
    
    public Quaternion getOrientation() {
        return new Quaternion(sceneBody.getOrientation().getW(), sceneBody.getOrientation().getX(), 
                sceneBody.getOrientation().getY(), sceneBody.getOrientation().getZ());
    }
    
    public void setOrientation(Quaternion orientation) {
        sceneBody.getOrientation().setW(orientation.getW());
        sceneBody.getOrientation().setX(orientation.getX());
        sceneBody.getOrientation().setY(orientation.getY());
        sceneBody.getOrientation().setZ(orientation.getZ());
    }
    
    private void updateVertex(Vertex vert, int offset, Quaternion orient, Vector3D loc) {
        Vector3D pos = new Vector3D(vert.getPosition().getX(),
                vert.getPosition().getY(), vert.getPosition().getZ());
        pos = orient.transform(pos).add(loc);
        pos.toDoubleArray(coordinates, offset);
    }
    
    private void buildArrays() {
        Mesh mesh = sceneBody.getMesh();
        coordinates = new double[3*mesh.getVertices().size()];
        normals = new float[3*mesh.getVertices().size()];
        vertices = new MeshVertex[mesh.getVertices().size()];
        vertexMap = new java.util.HashMap<Vertex, MeshVertex>();
        int i = 0;
        Quaternion orient = getOrientation(); Vector3D loc = getLocation();
        for (Vertex v : mesh.getVertices()) {
            updateVertex(v, 3*i, orient, loc);
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
        geometry = new IndexedTriangleArray(sceneBody.getMesh().getVertices().size(),
                IndexedTriangleArray.COORDINATES |
                IndexedTriangleArray.NORMALS |
                IndexedTriangleArray.BY_REFERENCE |
                IndexedTriangleArray.USE_COORD_INDEX_ONLY,
                3*sceneBody.getMesh().getFaces().size());
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
        appearance.setMaterial(sceneBody.getMesh().getMaterial().getJava3D());
        shape = new Shape3D(geometry, appearance);
    }


    public void processStimulus() {
        geometry.updateData(myUpdater);
    }

    public Node getJava3D() {
        return shape;
    }

    
    private class MyUpdater implements GeometryUpdater {
        public void updateData(Geometry geometry) {
            Quaternion orient = getOrientation(); Vector3D loc = getLocation();
            int coordIndex = 0;
            for (Vertex vert : sceneBody.getMesh().getVertices()) {
                updateVertex(vert, coordIndex, orient, loc);
                coordIndex += 3;
            }
            volume.updateBBox();
        }
    }
}

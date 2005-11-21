package de.kleppmann.maniation.geometry;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryUpdater;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.Shape3D;

import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.scene.Bone;
import de.kleppmann.maniation.scene.Deform;
import de.kleppmann.maniation.scene.Mesh;
import de.kleppmann.maniation.scene.Vertex;

public class MeshDeformation implements GeometryUpdater {
    
    private Mesh mesh;
    private double[] coordinates;
    private float[] normals;
    private IndexedTriangleArray geometry;
    private Shape3D shape;

    public MeshDeformation(Mesh mesh) {
        this.mesh = mesh;
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
        for (int i=0; i<mesh.getFaces().size(); i++) {
            for (int j=0; j<3; j++) {
                int index = mesh.getVertices().indexOf(
                        mesh.getFaces().get(i).getVertices().get(j));
                geometry.setCoordinateIndex(3*i+j, index);
            }
        }
        Appearance appearance = new Appearance();
        appearance.setMaterial(mesh.getMaterial().getJava3D());
        shape = new Shape3D(geometry, appearance);
    }
    
    public Shape3D getShape3D() {
        return shape;
    }
    
    public IndexedTriangleArray getGeometry() {
        return geometry;
    }
    
    public Mesh getMesh() {
        return mesh;
    }
    
    private Vector worldToBone(Vector x, Bone bone) {
        Vector boneBase = new Vector(
                bone.getBase().getX(),
                bone.getBase().getY(),
                bone.getBase().getZ());
        Quaternion boneOrient = new Quaternion(
                bone.getOrientation().getW(),
                bone.getOrientation().getX(),
                bone.getOrientation().getY(),
                bone.getOrientation().getZ());
        return boneOrient.getInverse().transform(x.subtract(boneBase));        
    }

    private Vector boneToWorld(Vector x, Bone bone) {
        Vector boneBase = new Vector(
                bone.getBase().getX(),
                bone.getBase().getY(),
                bone.getBase().getZ());
        Quaternion boneOrient = new Quaternion(
                bone.getOrientation().getW(),
                bone.getOrientation().getX(),
                bone.getOrientation().getY(),
                bone.getOrientation().getZ());
        return boneOrient.transform(x).add(boneBase);        
    }

    public void updateData(Geometry arg0) {
        //int coordIndex = 0;
        //for (Vertex vert : mesh.getVertices()) {
            /*Vector pos = new Vector(
                    vert.getPosition().getX(),
                    vert.getPosition().getY(),
                    vert.getPosition().getZ());*/
            /*Vector deformed = new Vector(0.0, 0.0, 0.0);
            for (Deform deform : vert.getDeforms()) {
                deformed.add(
                        boneToWorld(
                                worldToBone(pos, deform.getBone()),
                                deform.getBone()
                        ).mult(deform.getWeight()));
            }
            deformed*///pos.toDoubleArray(coordinates, 3*coordIndex);
            //coordIndex++;
        //}
    }
}

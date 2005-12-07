package de.kleppmann.maniation.geometry;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GeometryUpdater;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;

import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.scene.Bone;
import de.kleppmann.maniation.scene.Deform;
import de.kleppmann.maniation.scene.Mesh;
import de.kleppmann.maniation.scene.Vertex;
import de.realityinabox.util.Pair;

public class MeshDeformation implements GeometryUpdater {
    
    public static final boolean DRAW_SKELETON = true;
    
    private int frame = 0;
    private Mesh mesh;
    private double[] coordinates;
    private float[] normals;
    private GeometryArray geometry;
    private Shape3D shape;
    private java.util.Map<Bone,Pair<Vector,Quaternion>> skeletonRest, skeletonCurrent;
    private java.util.Map<Bone,Vector> boneEnds;

    public MeshDeformation(Mesh mesh) {
        this.mesh = mesh;
        if (DRAW_SKELETON) {
            buildSkeleton();
        } else {
            buildArrays();
            buildJava3D();
        }
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

    private void buildSkeleton() {
        coordinates = new double[12*mesh.getSkeleton().getBones().size()];
        updateSkeleton();
        LineArray lines = new LineArray(4*mesh.getSkeleton().getBones().size(),
                LineArray.COORDINATES | LineArray.COLOR_3 | LineArray.BY_REFERENCE);
        lines.setCapability(LineArray.ALLOW_REF_DATA_READ);
        lines.setCapability(LineArray.ALLOW_REF_DATA_WRITE);
        lines.setCapability(LineArray.ALLOW_COUNT_READ);
        lines.setCoordRefDouble(coordinates);
        float[] colours = new float[12*mesh.getSkeleton().getBones().size()];
        lines.setColorRefFloat(colours);
        for (int i=0; i<mesh.getSkeleton().getBones().size(); i++) {
            colours[12*i +  0] = 1.0f; colours[12*i +  1] = 1.0f; colours[12*i +  2] = 1.0f;
            colours[12*i +  3] = 1.0f; colours[12*i +  4] = 1.0f; colours[12*i +  5] = 1.0f;
            colours[12*i +  6] = 1.0f; colours[12*i +  7] = 0.0f; colours[12*i +  8] = 0.0f;
            colours[12*i +  9] = 1.0f; colours[12*i + 10] = 0.0f; colours[12*i + 11] = 0.0f;
        }
        geometry = lines;
        Appearance appearance = new Appearance();
        appearance.setMaterial(mesh.getMaterial().getJava3D());
        shape = new Shape3D(geometry, appearance);
    }

    private void updateSkeleton() {
        updateBones();
        for (int i=0; i<mesh.getSkeleton().getBones().size(); i++) {
            Bone b = mesh.getSkeleton().getBones().get(i);
            Pair<Vector,Quaternion> transform = skeletonCurrent.get(b);
            Vector base = transform.getLeft();
            Quaternion orient = transform.getRight();
            Vector end = orient.transform(boneEnds.get(b)).add(base);
            Vector xaxis = orient.transform(new Vector(0.1, 0, 0)).add(base);
            coordinates[12*i +  0] = base.getElement(0);
            coordinates[12*i +  1] = base.getElement(1);
            coordinates[12*i +  2] = base.getElement(2);
            coordinates[12*i +  3] = end.getElement(0);
            coordinates[12*i +  4] = end.getElement(1);
            coordinates[12*i +  5] = end.getElement(2);
            coordinates[12*i +  6] = base.getElement(0);
            coordinates[12*i +  7] = base.getElement(1);
            coordinates[12*i +  8] = base.getElement(2);
            coordinates[12*i +  9] = xaxis.getElement(0);
            coordinates[12*i + 10] = xaxis.getElement(1);
            coordinates[12*i + 11] = xaxis.getElement(2);
        }
    }
    
    public Shape3D getShape3D() {
        return shape;
    }
    
    public GeometryArray getGeometry() {
        return geometry;
    }
    
    public Mesh getMesh() {
        return mesh;
    }
    
    private Vector worldToBone(Vector x, Bone bone) {
        Pair<Vector,Quaternion> transform = skeletonRest.get(bone);
        Vector boneBase = transform.getLeft();
        Quaternion boneOrient = transform.getRight();
        return boneOrient.getInverse().transform(x.subtract(boneBase));        
    }

    private Vector boneToWorld(Vector x, Bone bone) {
        Pair<Vector,Quaternion> transform = skeletonCurrent.get(bone);
        Vector boneBase = transform.getLeft();
        Quaternion boneOrient = transform.getRight();
        return boneOrient.transform(x).add(boneBase);
    }

    public void updateData(Geometry arg0) {
        frame++;
        if (DRAW_SKELETON) {
            updateSkeleton();
            return;
        }
        int coordIndex = 0;
        updateBones();
        for (Vertex vert : mesh.getVertices()) {
            Vector pos = new Vector(
                    vert.getPosition().getX(),
                    vert.getPosition().getY(),
                    vert.getPosition().getZ());
            Vector deformed = new Vector(0.0, 0.0, 0.0);
            for (Deform deform : vert.getDeforms()) {
                deformed = deformed.add(
                        boneToWorld(
                                worldToBone(pos, deform.getBone()),
                                deform.getBone()
                        ).mult(deform.getWeight()));
            }
            deformed.toDoubleArray(coordinates, 3*coordIndex);
            coordIndex++;
        }
    }

    private void updateBones() {
        skeletonRest = new java.util.HashMap<Bone,Pair<Vector,Quaternion>>();
        skeletonCurrent = new java.util.HashMap<Bone,Pair<Vector,Quaternion>>();
        boneEnds = new java.util.HashMap<Bone,Vector>();
        for (Bone b : mesh.getSkeleton().getBones()) updateBone(b);
    }

    private void updateBone(Bone b) {
        if (skeletonRest.get(b) != null) return;
        Vector baseRest = new Vector(0, 0, 0);
        Quaternion orientRest = new Quaternion(1, 0, 0, 0);
        Vector baseCurrent = new Vector(0, 0, 0);
        Quaternion orientCurrent = new Quaternion(1, 0, 0, 0);
        if (b.getParentBone() != null) {
            updateBone(b.getParentBone());
            Pair<Vector,Quaternion> parentRest = skeletonRest.get(b.getParentBone());
            Pair<Vector,Quaternion> parentCurrent = skeletonCurrent.get(b.getParentBone());
            baseRest = parentRest.getLeft(); orientRest = parentRest.getRight();
            baseCurrent = parentCurrent.getLeft(); orientCurrent = parentCurrent.getRight();
        }
        Vector local = new Vector(b.getBase().getX(), b.getBase().getY(), b.getBase().getZ());
        baseRest = baseRest.add(orientRest.transform(local));
        baseCurrent = baseCurrent.add(orientCurrent.transform(local));
        orientRest = b.getOrientation().getValue().mult(orientRest);
        orientCurrent = b.getRotationAt(frame/30.0).mult(b.getOrientation().getValue().mult(orientCurrent));
        skeletonRest.put(b, new Pair<Vector,Quaternion>(baseRest, orientRest));
        skeletonCurrent.put(b, new Pair<Vector,Quaternion>(baseCurrent, orientCurrent));
        boneEnds.put(b.getParentBone(), local);
        boneEnds.put(b, new Vector(0, 0.1, 0));
    }
}

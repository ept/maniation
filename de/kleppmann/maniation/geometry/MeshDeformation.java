package de.kleppmann.maniation.geometry;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryUpdater;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.Shape3D;

import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.scene.Bone;
import de.kleppmann.maniation.scene.Deform;
import de.kleppmann.maniation.scene.Face;
import de.kleppmann.maniation.scene.Mesh;
import de.kleppmann.maniation.scene.Vertex;

public class MeshDeformation implements AnimateObject, GeometryUpdater {
    
    private Mesh mesh;
    private double[] coordinates;
    private float[] normals;
    private MeshTriangle[] triangles;
    private Map<Vertex, MeshVertex> vertexMap;
    private Map<Bone, CollisionVolume> boneVolumesMap;
    private Map<Bone, Set<Bone>> collisionTestBones;
    private IndexedTriangleArray geometry;
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
        vertexMap = new java.util.HashMap<Vertex, MeshVertex>();
        int i = 0;
        for (Vertex v : mesh.getVertices()) {
            coordinates[3*i+0] = v.getPosition().getX();
            coordinates[3*i+1] = v.getPosition().getY();
            coordinates[3*i+2] = v.getPosition().getZ();
            normals[3*i+0] = (float) v.getNormal().getX();
            normals[3*i+1] = (float) v.getNormal().getY();
            normals[3*i+2] = (float) v.getNormal().getZ();
            vertexMap.put(v, new MeshVertex(coordinates, i));
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
        boneVolumesMap = new java.util.HashMap<Bone, CollisionVolume>();
        Map<Vertex, Bone> vertexBonesMap = new java.util.HashMap<Vertex, Bone>();
        for (Bone bone : mesh.getSkeleton().getBones()) {
            List<MeshTriangle> boneTriangles = new java.util.ArrayList<MeshTriangle>();
            int triangleIndex = 0;
            for (Face face : mesh.getFaces()) {
                boolean faceDeformed = true;
                for (Vertex vert : face.getVertices()) {
                    if (vertexBonesMap.get(vert) != null) {
                        faceDeformed = false; continue;
                    }
                    boolean vertexDeformed = false;
                    for (Deform deform : vert.getDeforms())
                        if (deform.getBone() == bone) vertexDeformed = true;
                    if (!vertexDeformed) faceDeformed = false;
                }
                if (faceDeformed) {
                    boneTriangles.add(triangles[triangleIndex]);
                    for (Vertex vert : face.getVertices()) vertexBonesMap.put(vert, bone);
                }
                triangleIndex++;
            }
            MeshTriangle[] triArray = new MeshTriangle[boneTriangles.size()];
            triArray = boneTriangles.toArray(triArray);
            boneVolumesMap.put(bone, new CollisionVolume(triArray));
        }
        collisionTestBones = new java.util.HashMap<Bone, Set<Bone>>();
        for (i=0; i<mesh.getSkeleton().getBones().size(); i++) {
            Bone bone = mesh.getSkeleton().getBones().get(i);
            Set<Bone> boneList = new java.util.HashSet<Bone>();
            for (int j=0; j<i; j++)
                boneList.add(mesh.getSkeleton().getBones().get(j));
            collisionTestBones.put(bone, boneList);
        }
        for (Bone bone : mesh.getSkeleton().getBones()) {
            collisionTestBones.get(bone).remove(bone.getParentBone());
            Set<Bone> parentList = collisionTestBones.get(bone.getParentBone());
            if (parentList != null) parentList.remove(bone);
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
        /*for (Map.Entry<Bone, CollisionVolume> entry : boneVolumesMap.entrySet())
            entry.getValue().updateBBox();
        for (Map.Entry<Bone, CollisionVolume> entry : boneVolumesMap.entrySet()) {
            CollisionVolume vol1 = entry.getValue();
            for (Bone target : collisionTestBones.get(entry.getKey())) {
                Collision c = new Collision();
                //System.out.print(entry.getKey().getName() + " vs. " + target.getName() + ": ");
                boneVolumesMap.get(target).intersect(vol1, c);
                if (c.isColliding()) {
                    System.out.println(c.collisions + " collisions between " + 
                            entry.getKey().getName() + " and " + target.getName());
                }
            }
        }*/
    }
}

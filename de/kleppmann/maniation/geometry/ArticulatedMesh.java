package de.kleppmann.maniation.geometry;

import java.util.Map;
import java.util.Set;

import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryUpdater;

import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.scene.Body;
import de.kleppmann.maniation.scene.Bone;
import de.kleppmann.maniation.scene.Deform;
import de.kleppmann.maniation.scene.Face;
import de.kleppmann.maniation.scene.Mesh;
import de.kleppmann.maniation.scene.Vertex;

public class ArticulatedMesh extends AnimateMesh {
    
    private Map<MeshVertex, Bone> vertexBoneMap;
    private Map<MeshTriangle, Bone> triangleBoneMap;
    private Map<Bone, CollisionVolume> boneVolumeMap;
    private Map<Bone, Set<Bone>> collisionTestBones;
    private AnimateSkeleton skeleton;
    private MyUpdater myUpdater;

    public ArticulatedMesh(Body sceneBody) {
        super(sceneBody);
        this.skeleton = new AnimateSkeleton(sceneBody.getMesh().getSkeleton());
        compile();
    }
    
    private void compile() {
        Mesh mesh = sceneBody.getMesh();
        // Associate each vertex with the bone which affects it most
        vertexBoneMap = new java.util.HashMap<MeshVertex, Bone>();
        int vertexIndex = 0;
        for (Vertex vert : mesh.getVertices()) {
            double max = 0.0; Bone maxBone = null;
            for (Deform deform : vert.getDeforms()) {
                if (deform.getWeight() > max) {
                    max = deform.getWeight();
                    maxBone = deform.getBone();
                }
            }
            vertexBoneMap.put(vertices[vertexIndex], maxBone);
            vertexIndex++;
        }
        // Associate each face with the bone which affects the face's vertices most on average
        triangleBoneMap = new java.util.HashMap<MeshTriangle, Bone>();
        Map<Bone, Set<MeshTriangle>> boneTrianglesMap = new java.util.HashMap<Bone, Set<MeshTriangle>>();
        Map<Bone, Set<MeshVertex>> boneVerticesMap = new java.util.HashMap<Bone, Set<MeshVertex>>();
        for (Bone bone : mesh.getSkeleton().getBones()) {
            boneTrianglesMap.put(bone, new java.util.HashSet<MeshTriangle>());
            boneVerticesMap.put(bone, new java.util.HashSet<MeshVertex>());
        }
        int triangleIndex = 0;
        for (Face face : mesh.getFaces()) {
            Map<Bone, Double> boneWeight = new java.util.HashMap<Bone, Double>();
            for (Vertex vert : face.getVertices()) {
                double sum = 0.0;
                for (Deform deform : vert.getDeforms()) sum += deform.getWeight();
                for (Deform deform : vert.getDeforms()) {
                    Double w = boneWeight.get(deform.getBone());
                    boneWeight.put(deform.getBone(), (w == null) ? deform.getWeight()/sum : 
                        deform.getWeight()/sum + w);
                }
            }
            double max = 0.0; Bone maxBone = null;
            for (Map.Entry<Bone, Double> entry : boneWeight.entrySet()) {
                if (entry.getValue() > max) {
                    maxBone = entry.getKey();
                    max = entry.getValue();
                }
            }
            triangleBoneMap.put(triangles[triangleIndex], maxBone);
            boneTrianglesMap.get(maxBone).add(triangles[triangleIndex]);
            Set<MeshVertex> mvs = boneVerticesMap.get(maxBone);
            for (MeshVertex mv : triangles[triangleIndex].getVertices()) mvs.add(mv);
            triangleIndex++;
        }
        // Take the list of triangles for each bone and bake them together into a
        // collision/bounding volume.
        boneVolumeMap = new java.util.HashMap<Bone, CollisionVolume>();
        for (Bone bone : mesh.getSkeleton().getBones()) {
            Set<MeshTriangle> triangles = boneTrianglesMap.get(bone);
            MeshTriangle[] triArray = new MeshTriangle[triangles.size()];
            triArray = triangles.toArray(triArray);
            boneVolumeMap.put(bone, new CollisionVolume(triArray));
        }
        // Determine which bones should be tested for collision against each other.
        // Tests should not be symmetric (if A is tested against B, B should not be tested
        // against A), and any two bones whose triangle sets share a vertex (i.e. they are
        // directly adjacent parts of the mesh) should not be tested against each other.
        collisionTestBones = new java.util.HashMap<Bone, Set<Bone>>();
        for (int i=0; i<mesh.getSkeleton().getBones().size(); i++) {
            Bone bone = mesh.getSkeleton().getBones().get(i);
            Set<Bone> boneSet = new java.util.HashSet<Bone>();
            for (int j=0; j<i; j++) {
                Bone other = mesh.getSkeleton().getBones().get(j);
                Set<MeshVertex> intersect = new java.util.HashSet<MeshVertex>();
                intersect.addAll(boneVerticesMap.get(bone));
                intersect.retainAll(boneVerticesMap.get(other));
                if (intersect.size() == 0) boneSet.add(other);
            }
            collisionTestBones.put(bone, boneSet);
        }
    }
    
    public void processStimulus() {
        geometry.updateData(myUpdater);
        super.processStimulus();
    }
    
    
    private class MyUpdater implements GeometryUpdater {
        public void updateData(Geometry geometry) {
            skeleton.processStimulus();
            int coordIndex = 0;
            for (Vertex vert : sceneBody.getMesh().getVertices()) {
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
}

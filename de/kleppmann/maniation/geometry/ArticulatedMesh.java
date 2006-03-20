package de.kleppmann.maniation.geometry;

import java.util.Map;
import java.util.Set;

import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryUpdater;

import de.kleppmann.maniation.dynamics.ArticulatedBody;
import de.kleppmann.maniation.dynamics.GeneralizedBody;
import de.kleppmann.maniation.dynamics.StateVector;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.scene.Bone;
import de.kleppmann.maniation.scene.Deform;
import de.kleppmann.maniation.scene.Face;
import de.kleppmann.maniation.scene.Mesh;
import de.kleppmann.maniation.scene.Vertex;

public class ArticulatedMesh extends AnimateMesh {
    
    private Map<MeshVertex, Bone> vertexBoneMap;
    private Map<MeshTriangle, Bone> triangleBoneMap;
    private Map<Bone, ArticulatedLimb> boneLimbMap;
    private Map<Bone, Set<MeshTriangle>> boneTrianglesMap;
    private Map<Bone, Set<MeshVertex>> boneVerticesMap;
    private ArticulatedLimb[] limbList;
    private MyUpdater myUpdater = new MyUpdater();
    private ArticulatedBody dynamicBody;

    public ArticulatedMesh(de.kleppmann.maniation.scene.Body sceneBody) {
        super(sceneBody);
        super.setDynamicBody(null);
        compile();
    }
    
    public Map<MeshVertex, Bone>        getVertexBoneMap()    { return vertexBoneMap; }
    public Map<MeshTriangle, Bone>      getTriangleBoneMap()  { return triangleBoneMap; }
    public Map<Bone, ArticulatedLimb>   getBoneLimbMap()      { return boneLimbMap; }
    public Map<Bone, Set<MeshTriangle>> getBoneTrianglesMap() { return boneTrianglesMap; }
    public Map<Bone, Set<MeshVertex>>   getBoneVerticesMap()  { return boneVerticesMap; }
    public ArticulatedLimb[]            getLimbList()         { return limbList; }

    @Override
    public CollisionVolume getCollisionVolume() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ArticulatedBody getDynamicBody() {
        return dynamicBody;
    }

    @Override
    public void setDynamicBody(GeneralizedBody dynamicBody) {
        if (!(dynamicBody instanceof ArticulatedBody)) throw new IllegalArgumentException();
        this.dynamicBody = (ArticulatedBody) dynamicBody;
    }

    @Override
    public void setDynamicState(GeneralizedBody.State state, Vector3D com) {
        if (!(state instanceof StateVector)) throw new IllegalArgumentException();
        StateVector sv = (StateVector) state;
        for (int i=0; i<limbList.length; i++) {
            limbList[i].setDynamicState(sv.getSlice(i), dynamicBody.getBody(i).getCentreOfMass());
        }
        processStimulus();
        for (ArticulatedLimb limb : limbList) limb.getCollisionVolume().updateBBox();
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
        boneTrianglesMap = new java.util.HashMap<Bone, Set<MeshTriangle>>();
        boneVerticesMap = new java.util.HashMap<Bone, Set<MeshVertex>>();
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
        // Take the set of triangles for each bone and bake them together into an
        // ArticulatedLimb object.
        boneLimbMap = new java.util.HashMap<Bone, ArticulatedLimb>();
        limbList = new ArticulatedLimb[mesh.getSkeleton().getBones().size()];
        int limbNumber = 0;
        while (limbNumber < limbList.length) {
            for (Bone bone : mesh.getSkeleton().getBones()) {
                if (boneLimbMap.get(bone) == null) {
                    ArticulatedLimb parent = null;
                    if (bone.getParentBone() != null) parent = boneLimbMap.get(bone.getParentBone());
                    if ((parent != null) || (bone.getParentBone() == null)) {
                        ArticulatedLimb limb = new ArticulatedLimb(boneTrianglesMap.get(bone), bone, parent, this);
                        boneLimbMap.put(bone, limb);
                        limbList[limbNumber] = limb;
                        limbNumber++;
                    }
                }
            }
        }
    }

    public Vector3D currentVertexPosition(Vertex vert) {
        Vector3D pos = new Vector3D(vert.getPosition().getX(), vert.getPosition().getY(),
                vert.getPosition().getZ());
        Vector3D deformed = new Vector3D(0.0, 0.0, 0.0);
        for (Deform deform : vert.getDeforms()) {
            Vector3D world = boneLimbMap.get(deform.getBone()).currentVertexPosition(pos);
            deformed = deformed.add(world.mult(deform.getWeight()));
        }
        return deformed;
    }

    @Override
    public void processStimulus() {
        if (geometry != null) geometry.updateData(myUpdater); else myUpdater.updateData(null);
    }
    
    @Override
    public String toString() {
        String result = "";
        for (int i=0; i<limbList.length; i++) result += limbList[i] + " ";
        return result;
    }
    
    
    private class MyUpdater implements GeometryUpdater {
        public void updateData(Geometry geometry) {
            int coordIndex = 0;
            for (Vertex vert : sceneBody.getMesh().getVertices()) {
                currentVertexPosition(vert).toDoubleArray(coordinates, coordIndex);
                coordIndex += 3;
            }
        }
    }
}

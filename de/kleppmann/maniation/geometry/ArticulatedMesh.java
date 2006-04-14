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
        for (Vertex vert : mesh.getVertices()) {
            double max = 0.0; Bone maxBone = null;
            for (Deform deform : vert.getDeforms()) {
                if (deform.getWeight() > max) {
                    max = deform.getWeight();
                    maxBone = deform.getBone();
                }
            }
            vertexBoneMap.put(vertexMap.get(vert), maxBone);
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

    public void updateVertexPosition(Vertex vert, int offset) {
        Vector3D pos = new Vector3D(vert.getPosition().getX(), vert.getPosition().getY(),
                vert.getPosition().getZ());
        Vector3D norm = (new Vector3D(vert.getNormal().getX(),
                vert.getNormal().getY(), vert.getNormal().getZ())).normalize();
        Vector3D newPos = new Vector3D(), newNorm = new Vector3D();
        for (Deform deform : vert.getDeforms()) {
            ArticulatedLimb limb = boneLimbMap.get(deform.getBone());
            Vector3D limbPos = limb.currentVertexPosition(pos);
            newPos = newPos.add(limbPos.mult(deform.getWeight()));
            Vector3D limbNorm = limb.getCurrentOrientation().transform(
                    limb.getRestOrientation().getInverse().transform(norm));
            newNorm = newNorm.add(limbNorm.mult(deform.getWeight()));
        }
        newPos.toDoubleArray(coordinates, offset);
        newNorm = newNorm.normalize();
        for (int i=0; i<3; i++) normals[offset+i] = (float) newNorm.getComponent(i);
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
                updateVertexPosition(vert, coordIndex);
                coordIndex += 3;
            }
            ArticulatedMesh.super.getCollisionVolume().updateBBox();
            for (ArticulatedLimb limb : limbList) limb.updateBubbles();
            /*try {
                java.io.FileWriter fw = new java.io.FileWriter("debug.ps");
                fw.write("0.01 setlinewidth 82.5 45 moveto 82.5 75 lineto stroke 82.5 75 moveto 120 75 lineto stroke ");
                java.text.DecimalFormat format = new java.text.DecimalFormat("###0.00000");
                for (int i=0; i<coordinates.length/3; i++) {
                    double x = 150*(0.5+coordinates[3*i+1]);
                    double y = 150*(0.5+coordinates[3*i+2]);
                    fw.write(format.format(x) + " " + format.format(y) + " moveto " +
                            format.format(x-0.01) + " " + format.format(y) + " lineto stroke ");
                }
                fw.write("showpage");
                fw.close();
            } catch (Exception e) {
                System.err.println(e);
            }*/
        }
    }
}

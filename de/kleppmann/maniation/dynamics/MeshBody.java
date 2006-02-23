package de.kleppmann.maniation.dynamics;

import java.util.List;

import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.scene.Vertex;

public class MeshBody extends Cylinder {
    
    private MeshInfo info;
    
    private MeshBody(MeshInfo info) {
        super(info.axis, info.radius, info.length, info.mass);
        setCoMPosition(info.com);
        this.info = info;
    }
    
    public static MeshBody newMeshBody(de.kleppmann.maniation.scene.Body sceneBody) {
        MeshInfo info = new MeshInfo(sceneBody);
        return new MeshBody(info);
    }
    
    public Vector3D getLocation() {
        return getCoMPosition().subtract(info.com);
    }
    
    
    private static class MeshInfo {

        double radius, length, mass;
        Vector3D axis, com;
        
        MeshInfo(de.kleppmann.maniation.scene.Body sceneBody) {
            axis = axis.normalize();
            this.axis = new Vector3D(sceneBody.getAxis().getX(), sceneBody.getAxis().getY(),
                    sceneBody.getAxis().getZ());;
            List<Vector3D> points = new java.util.ArrayList<Vector3D>();
            // Approximate location of the centre of mass by averaging all vertex positions
            com = new Vector3D();
            for (Vertex vert : sceneBody.getMesh().getVertices()) {
                Vector3D pos = new Vector3D(vert.getPosition().getX(), vert.getPosition().getY(),
                        vert.getPosition().getZ());
                points.add(pos);
                com = com.add(pos);
            }
            com = com.mult(1.0/points.size());
            // Radius is the maximum distance from the axis (straight line through CoM).
            // Length is the difference between the maximum and minimum positions
            // projected onto the axis.
            radius = 0.0;
            double lmin = 1e20, lmax = -1e20;
            for (Vector3D v : points) {
                Vector3D vr = v.subtract(com);
                double r = vr.cross(axis).magnitude();
                if (r > radius) radius = r;
                double l = vr.mult(axis);
                if (l > lmax) lmax = l;
                if (l < lmin) lmin = l;
            }
            length = lmax - lmin;
            // Calculate mass based on volume and density
            mass = sceneBody.getMesh().getMaterial().getDensity()*Math.PI*radius*radius*length;
        }
    }
}

package de.kleppmann.maniation.dynamics;

import java.util.List;

import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.scene.Mesh;
import de.kleppmann.maniation.scene.Vertex;

public class MeshBody extends Cylinder {
    
    private MeshInfo info;
    
    private MeshBody(MeshInfo info) {
        super(info.axis, info.radius, info.length, info.mass);
        this.info = info;
    }
    
    public static MeshBody newMeshBody(Mesh mesh, Vector3D axis, double density) {
        MeshInfo info = new MeshInfo(mesh, axis, density);
        return new MeshBody(info);
    }
    
    public Vector3D getLocation() {
        return getCoMPosition().add(info.com);
    }
    
    private static class MeshInfo {

        Mesh mesh;
        double radius, length, mass, density;
        Vector3D axis, com;
        
        MeshInfo(Mesh mesh, Vector3D axis, double density) {
            this.mesh = mesh;
            axis = axis.normalize();
            this.axis = axis;
            this.density = density;
            List<Vector3D> points = new java.util.ArrayList<Vector3D>();
            // Approximate location of the centre of mass by averaging all vertex positions
            com = new Vector3D();
            for (Vertex vert : mesh.getVertices()) {
                Vector3D pos = new Vector3D(vert.getPosition().getX(), vert.getPosition().getY(),
                        vert.getPosition().getZ());
                points.add(pos);
                com.add(pos);
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
            mass = density*Math.PI*radius*radius*length;
        }
    }
}

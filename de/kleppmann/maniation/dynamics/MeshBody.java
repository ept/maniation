package de.kleppmann.maniation.dynamics;

import java.util.List;

import de.kleppmann.maniation.geometry.AnimateMesh;
import de.kleppmann.maniation.geometry.Collision;
import de.kleppmann.maniation.geometry.CollisionVolume;
import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.scene.Vertex;

public class MeshBody extends Cylinder implements Collideable {
    
    private AnimateMesh mesh;
    private MeshInfo info;
    
    private MeshBody(AnimateMesh mesh, MeshInfo info) {
        super(info.axis, info.radius, info.length, info.mass);
        setCoMPosition(info.com);
        this.info = info;
    }
    
    public static MeshBody newMeshBody(AnimateMesh mesh) {
        MeshInfo info = new MeshInfo(mesh);
        return new MeshBody(mesh, info);
    }
    
    protected void setCoMPosition(Vector3D pos) {
        super.setCoMPosition(pos);
        mesh.setLocation(getLocation());
    }
    
    protected void setOrientation(Quaternion orient) {
        super.setOrientation(orient);
        mesh.setOrientation(getOrientation());
    }
    
    public Vector3D getLocation() {
        return getCoMPosition().subtract(getOrientation().transform(info.com));
    }
    
    protected void setLocation(Vector3D location) {
        setCoMPosition(location.add(getOrientation().transform(info.com)));
    }

    public void interaction(SimulationObject partner, InteractionList result, boolean allowReverse) {
        if (partner instanceof Collideable) {
            ((Collideable) partner).collideWith(this, mesh.getCollisionVolume(), result);
        } else super.interaction(partner, result, allowReverse);
    }

    public void collideWith(RigidBody body, CollisionVolume volume, InteractionList result) {
        Collision collision = new Collision();
        mesh.getCollisionVolume().intersect(volume, collision);
        result.processCollision(collision);
    }

    
    private static class MeshInfo {

        double radius, length, mass;
        Vector3D axis, com;
        
        MeshInfo(AnimateMesh mesh) {
            de.kleppmann.maniation.scene.Vector vaxis = mesh.getSceneBody().getAxis();
            axis = new Vector3D(vaxis.getX(), vaxis.getY(), vaxis.getZ());
            axis = axis.normalize();
            List<Vector3D> points = new java.util.ArrayList<Vector3D>();
            // Approximate location of the centre of mass by averaging all vertex positions
            com = new Vector3D();
            for (Vertex vert : mesh.getSceneBody().getMesh().getVertices()) {
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
            mass = mesh.getSceneBody().getMesh().getMaterial().getDensity()*Math.PI*radius*radius*length;
        }
    }
}

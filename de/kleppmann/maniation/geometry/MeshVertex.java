package de.kleppmann.maniation.geometry;

import java.util.Set;

import de.kleppmann.maniation.maths.Vector3D;


public class MeshVertex {
    
    public enum Component { 
        X(0), Y(1), Z(2);
        int offset;
        private Component(int offset) { this.offset = offset; }
    };
    
    private final double[] coordinates;
    private final float[] normals;
    private final Set<Integer> indices;

    public MeshVertex(double[] coordinates, float[] normals, Set<Integer> indices) {
        this.coordinates = coordinates;
        this.normals = normals;
        this.indices = indices;
    }
    
    public Set<Integer> getIndices() {
        return indices;
    }

    public double getComponent(Component c) {
        double result = 0.0;
        for (Integer i : indices) result += coordinates[3*i + c.offset];
        result /= indices.size();
        return result;
    }
    
    public Vector3D getPosition() {
        return new Vector3D(getComponent(Component.X), getComponent(Component.Y), getComponent(Component.Z));
    }
    
    public Vector3D getNormal() {
        double x=0.0, y=0.0, z=0.0;
        for (Integer i : indices) {
            x += normals[3*i]; y += normals[3*i+1]; z += normals[3*i+2];
        }
        return (new Vector3D(x, y, z)).normalize();
    }
    
    public double max3(MeshVertex other1, MeshVertex other2, Component c) {
        double val1 = this.getComponent(c);
        double val2 = other1.getComponent(c);
        double val3 = other2.getComponent(c);
        return (val1 > val2) && (val1 > val3) ? val1 : (val2 > val3 ? val2 : val3);
    }

    public double min3(MeshVertex other1, MeshVertex other2, Component c) {
        double val1 = this.getComponent(c);
        double val2 = other1.getComponent(c);
        double val3 = other2.getComponent(c);
        return (val1 < val2) && (val1 < val3) ? val1 : (val2 < val3 ? val2 : val3);
    }

    public double avg3(MeshVertex other1, MeshVertex other2, Component c) {
        double val1 = this.getComponent(c);
        double val2 = other1.getComponent(c);
        double val3 = other2.getComponent(c);
        return (val1 + val2 + val3) / 3.0;
    }
}

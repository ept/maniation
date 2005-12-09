package de.kleppmann.maniation.geometry;


public class MeshVertex {
    
    public enum Component { 
        X(0), Y(1), Z(2);
        int offset;
        private Component(int offset) { this.offset = offset; }
    };
    
    double[] coordinates;
    int index;

    public MeshVertex(double[] coordinates, int index) {
        this.coordinates = coordinates;
        this.index = index;
    }

    public double getComponent(Component c) {
        return coordinates[3*index + c.offset];
    }
    
    public double max3(MeshVertex other1, MeshVertex other2, Component c) {
        double val1 = coordinates[3*index + c.offset];
        double val2 = other1.coordinates[3*other1.index + c.offset];
        double val3 = other2.coordinates[3*other2.index + c.offset];
        return (val1 > val2) && (val1 > val3) ? val1 : (val2 > val3 ? val2 : val3);
    }

    public double min3(MeshVertex other1, MeshVertex other2, Component c) {
        double val1 = coordinates[3*index + c.offset];
        double val2 = other1.coordinates[3*other1.index + c.offset];
        double val3 = other2.coordinates[3*other2.index + c.offset];
        return (val1 < val2) && (val1 < val3) ? val1 : (val2 < val3 ? val2 : val3);
    }

    public double avg3(MeshVertex other1, MeshVertex other2, Component c) {
        double val1 = coordinates[3*index + c.offset];
        double val2 = other1.coordinates[3*other1.index + c.offset];
        double val3 = other2.coordinates[3*other2.index + c.offset];
        return (val1 + val2 + val3) / 3.0;
    }
}

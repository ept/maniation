package de.kleppmann.maniation.jointlimit;

import java.util.List;

public class Inequalities {
    
    private List<Inequality> functions = new java.util.ArrayList<Inequality>();
    
    public List<Inequality> getFunctions() {
        return functions;
    }
    
    public double[] eval(double x, double y, double z) {
        double[] result = new double[functions.size()];
        for (int i=0; i<functions.size(); i++)
            result[i] = functions.get(i).getValue(x, y, z);
        return result;
    }
    
    public boolean satisfiedAt(double x, double y, double z) {
        double[] val = eval(x, y, z);
        boolean result = true;
        for (int i=0; i<val.length; i++) if (val[i] < 0.0) result = false;
        return result;
    }
}

package de.kleppmann.maniation.maths;

import java.text.DecimalFormat;
import java.util.List;

public class VectorImpl implements Vector {

    private double values[];
    
    public VectorImpl(int dimension) {
        dimension = Math.max(dimension, 1);
        values = new double[dimension];
        for (int i=0; i<dimension; i++) values[i] = 0.0;
    }
    
    public VectorImpl(double[] values) {
        this.values = values;
        if ((values == null) || (values.length == 0)) {
            this.values = new double[1];
            this.values[0] = 0.0;
        }
    }
    
    public VectorImpl(String filename) {
        try {
            List<Double> list = new java.util.ArrayList<Double>();
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filename));
            java.io.StreamTokenizer tok = new java.io.StreamTokenizer(reader);
            tok.ordinaryChars('0', '9');
            tok.ordinaryChar('.');
            tok.ordinaryChar('-');
            tok.wordChars(0x0021, 0x00ff);
            tok.whitespaceChars(0x0000, 0x0020);
            tok.commentChar('#');
            tok.eolIsSignificant(true);
            while (true) {
                int token = tok.nextToken();
                if (token == java.io.StreamTokenizer.TT_EOF) break;
                if ((token == java.io.StreamTokenizer.TT_EOL) && (list.size() > 0)) break;
                if (token == java.io.StreamTokenizer.TT_NUMBER) list.add(tok.nval);
                if (token == java.io.StreamTokenizer.TT_WORD)
                    list.add(Double.parseDouble(tok.sval));
            }
            values = new double[list.size()];
            for (int i=0; i<values.length; i++) values[i] = list.get(i);
        } catch (java.io.IOException e) {
            this.values = null;
        }
    }

    @Override
    public String toString() {
        DecimalFormat format = new DecimalFormat("######0.000000000000000");
        String result = "";
        for (int i=0; i<getDimension(); i++) {
            if (!result.equals("")) result += ", ";
            result += format.format(getComponent(i));
        }
        return "Vector(" + result + ")";
    }

    public int getDimension() {
        return values.length;
    }
    
    public double getComponent(int index) {
        return values[index];
    }
    
    public Vector mult(double scalar) {
        double[] newv = new double[getDimension()];
        for (int i=0; i<getDimension(); i++) newv[i] = scalar*getComponent(i);
        return new VectorImpl(newv);
    }
    
    public double mult(Vector v) {
        if (v.getDimension() != getDimension()) throw new IllegalArgumentException();
        double result = 0.0;
        for (int i=0; i<getDimension(); i++)
            result += this.getComponent(i) * v.getComponent(i);
        return result;
    }
    
    public Vector multComponents(Vector v) {
        if (v.getDimension() != getDimension()) throw new IllegalArgumentException();
        double[] newv = new double[getDimension()];
        for (int i=0; i<getDimension(); i++)
            newv[i] = this.getComponent(i) * v.getComponent(i);
        return new VectorImpl(newv);
    }

    public Vector add(Vector v) {
        if (v.getDimension() != getDimension()) throw new IllegalArgumentException();
        double[] newv = new double[getDimension()];
        for (int i=0; i<getDimension(); i++)
            newv[i] = this.getComponent(i) + v.getComponent(i);
        return new VectorImpl(newv);
    }
    
    public Vector subtract(Vector v) {
        if (v.getDimension() != getDimension()) throw new IllegalArgumentException();
        double[] newv = new double[getDimension()];
        for (int i=0; i<getDimension(); i++)
            newv[i] = this.getComponent(i) - v.getComponent(i);
        return new VectorImpl(newv);
    }
    
    public void toDoubleArray(double[] array, int offset) {
        for (int i=getDimension()-1; i>=0; i--) array[offset+i] = getComponent(i);
    }
}

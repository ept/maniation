package de.realityinabox.databinding.types;

import de.realityinabox.databinding.sourcemodel.Package;

public abstract class NumericType extends PrimitiveType {

    private double minimum = 0.0;
    private boolean minimumSet = false;
    private double maximum = 0.0;
    private boolean maximumSet = false;

    protected NumericType(Package pack) {
        super(pack);
    }

    public double getMinimum() {
        return minimum;
    }

    public boolean isMinimumSet() {
        return minimumSet;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
        this.minimumSet = true;
    }

    public void unsetMinimum() {
        this.minimum = 0.0;
        this.minimumSet = false;
    }

    public double getMaximum() {
        return maximum;
    }

    public boolean isMaximumSet() {
        return maximumSet;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
        this.maximumSet = true;
    }

    public void unsetMaximum() {
        this.maximum = 0.0;
        this.maximumSet = false;
    }
}

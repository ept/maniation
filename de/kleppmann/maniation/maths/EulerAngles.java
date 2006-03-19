package de.kleppmann.maniation.maths;

import java.text.DecimalFormat;

public class EulerAngles {
    
    public enum Convention { ROLL_PITCH_YAW };
    
    private Convention conv;
    private double ang1, ang2, ang3;

    public EulerAngles(Convention conv, double ang1, double ang2, double ang3) {
        this.conv = conv; this.ang1 = ang1; this.ang2 = ang2; this.ang3 = ang3;
    }

    public Convention getAxisConvention() {
        return conv;
    }
    
    public double getAngle1() {
        return ang1;
    }
    
    public double getAngle2() {
        return ang2;
    }

    public double getAngle3() {
        return ang3;
    }
    
    public EulerAngles convertTo(Convention newConv) {
        return this;
    }
    
    public EulerAngles closeBy(EulerAngles previousAngles) {
        double a1 = ang1, a2 = ang2, a3 = ang3;
        while (a1 - previousAngles.getAngle1() >  Math.PI) a1 -= 2.0*Math.PI;
        while (a1 - previousAngles.getAngle1() < -Math.PI) a1 += 2.0*Math.PI;
        while (a2 - previousAngles.getAngle2() >  Math.PI) a2 -= 2.0*Math.PI;
        while (a2 - previousAngles.getAngle2() < -Math.PI) a2 += 2.0*Math.PI;
        while (a3 - previousAngles.getAngle3() >  Math.PI) a3 -= 2.0*Math.PI;
        while (a3 - previousAngles.getAngle3() < -Math.PI) a3 += 2.0*Math.PI;
        return new EulerAngles(this.conv, a1, a2, a3);
    }
    
    @Override
    public String toString() {
        DecimalFormat format = new DecimalFormat("######0.000000000000000");
        return format.format(ang1) + " " + format.format(ang2) + " " +
            format.format(ang3);
    }
}

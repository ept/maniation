package de.kleppmann.maniation.dynamics;

import java.util.Map;
import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.Vector;

public interface Constraint extends Interaction {
    Vector getPenalty();
    Vector getPenaltyDot();
    Map<Body,Matrix> getJacobian();
    Map<Body,Matrix> getJacobianDot();
    int getDimension();
}

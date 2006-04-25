package de.kleppmann.maniation.dynamics;

import java.util.Map;
import de.kleppmann.maniation.maths.Vector3D;

public interface InequalityConstraint extends Constraint {
    boolean isInequality();
    Map<Body, Vector3D> setToZero();
    double getElasticity();
}

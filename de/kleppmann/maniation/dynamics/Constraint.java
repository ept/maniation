package de.kleppmann.maniation.dynamics;

import java.util.Map;
import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.Vector;

public interface Constraint extends Interaction {
    Vector getPenalty();
    Vector getPenaltyDot();
    Map<RigidBody,Matrix> getJacobian();
    Map<RigidBody,Matrix> getJacobianDot();
}

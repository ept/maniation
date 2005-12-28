package de.kleppmann.maniation.dynamics;

import java.util.List;

public interface DynamicScene {
    List<RigidBody> getBodies();
    List<Constraint> getConstraints();
}

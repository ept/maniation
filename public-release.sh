#!/bin/sh

FILELIST=`tempfile`
cat > $FILELIST << EOF
    README
    LICENSE
    de/kleppmann/maniation/Centrifuge.java
    de/kleppmann/maniation/Gyroscope.java
    de/kleppmann/maniation/MultiPendulum.java
    de/kleppmann/maniation/NewtonsCradle.java
    de/kleppmann/maniation/dynamics/ArticulatedBody.java
    de/kleppmann/maniation/dynamics/Body.java
    de/kleppmann/maniation/dynamics/Collideable.java
    de/kleppmann/maniation/dynamics/CompoundBody.java
    de/kleppmann/maniation/dynamics/Constraint.java
    de/kleppmann/maniation/dynamics/Cylinder.java
    de/kleppmann/maniation/dynamics/DynamicScene.java
    de/kleppmann/maniation/dynamics/EdgeEdgeCollision.java
    de/kleppmann/maniation/dynamics/GeneralizedBody.java
    de/kleppmann/maniation/dynamics/InequalityConstraint.java
    de/kleppmann/maniation/dynamics/Interaction.java
    de/kleppmann/maniation/dynamics/InteractionForce.java
    de/kleppmann/maniation/dynamics/InteractionList.java
    de/kleppmann/maniation/dynamics/JointConstraint.java
    de/kleppmann/maniation/dynamics/MeshBody.java
    de/kleppmann/maniation/dynamics/NailConstraint.java
    de/kleppmann/maniation/dynamics/RigidBody.java
    de/kleppmann/maniation/dynamics/RotationConstraint.java
    de/kleppmann/maniation/dynamics/Simulation.java
    de/kleppmann/maniation/dynamics/SimulationObject.java
    de/kleppmann/maniation/dynamics/SphereEdgeCollision.java
    de/kleppmann/maniation/dynamics/SphereFaceCollision.java
    de/kleppmann/maniation/dynamics/SphereSphereCollision.java
    de/kleppmann/maniation/dynamics/StateVector.java
    de/kleppmann/maniation/dynamics/VertexFaceCollision.java
    de/kleppmann/maniation/dynamics/World.java
    de/kleppmann/maniation/geometry/AnimateMesh.java
    de/kleppmann/maniation/geometry/AnimateObject.java
    de/kleppmann/maniation/geometry/ArticulatedLimb.java
    de/kleppmann/maniation/geometry/ArticulatedMesh.java
    de/kleppmann/maniation/geometry/BoundingBox.java
    de/kleppmann/maniation/geometry/Collision.java
    de/kleppmann/maniation/geometry/CollisionVolume.java
    de/kleppmann/maniation/geometry/GeometryBehaviour.java
    de/kleppmann/maniation/geometry/InexactPoint.java
    de/kleppmann/maniation/geometry/MeshTriangle.java
    de/kleppmann/maniation/geometry/MeshVertex.java
    de/kleppmann/maniation/maths/ConjugateGradient.java
    de/kleppmann/maniation/maths/EulerAngles.java
    de/kleppmann/maniation/maths/Matrix.java
    de/kleppmann/maniation/maths/Matrix33.java
    de/kleppmann/maniation/maths/MatrixImpl.java
    de/kleppmann/maniation/maths/ODE.java
    de/kleppmann/maniation/maths/ODEBacktrackException.java
    de/kleppmann/maniation/maths/ODESolver.java
    de/kleppmann/maniation/maths/Quaternion.java
    de/kleppmann/maniation/maths/RungeKutta.java
    de/kleppmann/maniation/maths/SlicedVector.java
    de/kleppmann/maniation/maths/SparseMatrix.java
    de/kleppmann/maniation/maths/Vector.java
    de/kleppmann/maniation/maths/Vector3D.java
    de/kleppmann/maniation/maths/VectorImpl.java
    de/kleppmann/maniation/scene/Animation.java
    de/kleppmann/maniation/scene/AxisConstraint.java
    de/kleppmann/maniation/scene/Body.java
    de/kleppmann/maniation/scene/Bone.java
    de/kleppmann/maniation/scene/Bubble.java
    de/kleppmann/maniation/scene/Colour.java
    de/kleppmann/maniation/scene/Deform.java
    de/kleppmann/maniation/scene/Face.java
    de/kleppmann/maniation/scene/Keyframe.java
    de/kleppmann/maniation/scene/Material.java
    de/kleppmann/maniation/scene/Mesh.java
    de/kleppmann/maniation/scene/Quaternion.java
    de/kleppmann/maniation/scene/Scene.java
    de/kleppmann/maniation/scene/Skeleton.java
    de/kleppmann/maniation/scene/Vector.java
    de/kleppmann/maniation/scene/Vertex.java
EOF

DIR=`dirname "$0"`
COMMAND="zip -r maniation.zip"

echo "$COMMAND files..."
$COMMAND `cat $FILELIST`

rm -f $FILELIST

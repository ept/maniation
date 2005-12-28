package de.kleppmann.maniation.dynamics;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.SparseMatrix;

public class InertiaTensor extends SparseMatrix {
    
    public InertiaTensor(DynamicScene scene) {
        super(6*scene.getBodies().size(), 6*scene.getBodies().size(),
                makeSlices(scene));
    }

    private static SparseMatrix.Slice[] makeSlices(DynamicScene scene) {
        SparseMatrix.Slice[] result = new SparseMatrix.Slice[scene.getBodies().size()];
        for (int i=0; i<result.length; i++) result[i] = new InertiaSlice(scene, i);
        return result;
    }
    
    private static class InertiaSlice implements SparseMatrix.Slice {
        private int bodyIndex;
        private DynamicScene scene;
        
        private InertiaSlice(DynamicScene scene, int bodyIndex) {
            this.scene = scene; this.bodyIndex = bodyIndex;
        }
        
        public Matrix getMatrix() {
            return scene.getBodies().get(bodyIndex).getMassInertia();
        }

        public int getStartColumn() {
            return 6*bodyIndex;
        }

        public int getStartRow() {
            return 6*bodyIndex;
        }
    }
}

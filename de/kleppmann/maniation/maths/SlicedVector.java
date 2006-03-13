package de.kleppmann.maniation.maths;

public class SlicedVector <T extends Vector> implements Vector {
    
    private final T[] slices;
    private final int[] sliceOffsets;
    
    public SlicedVector(T[] slices) {
        this.slices = slices;
        int i=0, j=0;
        sliceOffsets = new int[slices.length + 1];
        for (T slice : slices) {
            sliceOffsets[i] = j;
            i++; j += slice.getDimension();
        }
        sliceOffsets[i] = j;
    }
    
    private SlicedVector(T[] slices, int[] sliceOffsets) {
        this.slices = slices; this.sliceOffsets = sliceOffsets;
    }
    
    public int getSlices() {
        return slices.length;
    }
    
    public T getSlice(int index) {
        return slices[index];
    }

    public int getDimension() {
        return sliceOffsets[sliceOffsets.length - 1];
    }

    public double getComponent(int index) {
        // Naive linear search. Replace this by binary chop.
        int i = sliceOffsets.length - 1;
        while (sliceOffsets[i] > index) i--;
        return slices[i].getComponent(index - sliceOffsets[i]);
    }

    @SuppressWarnings("unchecked")
    public SlicedVector<T> mult(double scalar) {
        try {
            T[] newSlices = (T[]) java.lang.reflect.Array.newInstance(
                    slices.getClass().getComponentType(), slices.length);
            for (int i=0; i<slices.length; i++) newSlices[i] = (T) slices[i].mult(scalar);
            return new SlicedVector<T>(newSlices, sliceOffsets);
        } catch (ClassCastException e) {
            throw e;
        }
    }

    public double mult(Vector v) {
        if (!(v instanceof SlicedVector)) throw new IllegalArgumentException();
        SlicedVector other = (SlicedVector) v;
        if (!sliceOffsets.equals(other.sliceOffsets)) throw new IllegalArgumentException();
        double sum = 0.0;
        for (int i=0; i<slices.length; i++) sum += slices[i].mult(other.slices[i]);
        return sum;
    }

    // Rubbish copied and pasted implementation of the following 3 methods.
    // But Java's generics are rather strange too. Why is it not possible
    // to create an array of a generic type without going via reflection?
    @SuppressWarnings("unchecked")
    public SlicedVector<T> multComponents(Vector v) {
        if (!(v instanceof SlicedVector)) throw new IllegalArgumentException();
        SlicedVector other = (SlicedVector) v;
        if (!sliceOffsets.equals(other.sliceOffsets)) throw new IllegalArgumentException();
        try {
            T[] newSlices = (T[]) java.lang.reflect.Array.newInstance(
                    slices.getClass().getComponentType(), slices.length);
            for (int i=0; i<slices.length; i++)
                newSlices[i] = (T) slices[i].multComponents(other.slices[i]);
            return new SlicedVector<T>(newSlices, sliceOffsets);
        } catch (ClassCastException e) {
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public SlicedVector<T> add(Vector v) {
        if (!(v instanceof SlicedVector)) throw new IllegalArgumentException();
        SlicedVector other = (SlicedVector) v;
        if (!sliceOffsets.equals(other.sliceOffsets)) throw new IllegalArgumentException();
        try {
            T[] newSlices = (T[]) java.lang.reflect.Array.newInstance(
                    slices.getClass().getComponentType(), slices.length);
            for (int i=0; i<slices.length; i++)
                newSlices[i] = (T) slices[i].add(other.slices[i]);
            return new SlicedVector<T>(newSlices, sliceOffsets);
        } catch (ClassCastException e) {
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public SlicedVector<T> subtract(Vector v) {
        if (!(v instanceof SlicedVector)) throw new IllegalArgumentException();
        SlicedVector other = (SlicedVector) v;
        if (!sliceOffsets.equals(other.sliceOffsets)) throw new IllegalArgumentException();
        try {
            T[] newSlices = (T[]) java.lang.reflect.Array.newInstance(
                    slices.getClass().getComponentType(), slices.length);
            for (int i=0; i<slices.length; i++)
                newSlices[i] = (T) slices[i].subtract(other.slices[i]);
            return new SlicedVector<T>(newSlices, sliceOffsets);
        } catch (ClassCastException e) {
            throw e;
        }
    }

    public void toDoubleArray(double[] array, int offset) {
        for (int i=0; i<slices.length; i++) slices[i].toDoubleArray(array, offset + sliceOffsets[i]);
    }

    public String toString() {
        String result = "";
        for (int i=0; i<slices.length; i++) result += (i == 0 ? "" : " ") + slices[i].toString();
        return result;
    }
}

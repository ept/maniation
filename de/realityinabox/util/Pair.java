package de.realityinabox.util;

public class Pair<S,T> {
    private S left;
    private T right;
    
    public Pair(S left, T right) {
        this.left = left;
        this.right = right;
    }

    public S getLeft() {
        return left;
    }
    
    public T getRight() {
        return right;
    }
    
    public boolean equals(Object o) {
        if (o instanceof Pair) {
            return ((Pair) o).left.equals(this.left) &&
                   ((Pair) o).right.equals(this.right);
        }
        return false;
    }
    
    public int hashCode() {
        return left.hashCode() ^ ~right.hashCode();
    }
    
    public String toString() {
        return "(" + left.toString() + ", " + right.toString() + ")";
    }
}

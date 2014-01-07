package de.realityinabox.util;

public class CommutativePair<T> extends Pair<T,T> {
    
    public CommutativePair(T left, T right) {
        super(left, right);
    }
    
    public CommutativePair reverse() {
        return new CommutativePair<T>(getRight(), getLeft());
    }
    
    public T getLeft() {
        return super.getLeft();
    }

    public T getRight() {
        return super.getRight();
    }

    public boolean equals(Object o) {
        if (o instanceof CommutativePair) {
            CommutativePair other = (CommutativePair) o;
            return (other.getLeft().equals(this.getLeft()) && other.getRight().equals(this.getRight())) ||
                (other.getLeft().equals(this.getRight()) && other.getRight().equals(this.getLeft()));
        } else return false;
    }
    
    public int hashCode() {
        return getLeft().hashCode() ^ getRight().hashCode();
    }
}

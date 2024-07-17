package edu.hanyang.utils;

public class Tuple<T> {

    protected final T left;
    protected final T right;

    public Tuple(final T left, final T right) {
        this.left = left;
        this.right = right;
    }

    public T getLeft() { return left; }
    public T getRight() { return right; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple) {
            Tuple<T> other = (Tuple<T>) obj;
            return this.left == other.left && this.right == other.right;
        }
        return false;
    }
}

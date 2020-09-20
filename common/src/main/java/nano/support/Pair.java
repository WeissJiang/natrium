package nano.support;

import java.util.Objects;

/**
 * Utility class representing a pair of values.
 */
public final class Pair<L, R> {

    private static final Pair<?, ?> EMPTY = new Pair<>(null, null);

    private final L left;
    private final R right;

    /**
     * Returns an empty pair.
     */
    @SuppressWarnings("unchecked")
    public static <L, R> Pair<L, R> empty() {
        return (Pair<L, R>) EMPTY;
    }

    /**
     * Constructs a pair with its left value being {@code left}, or returns an empty pair if
     * {@code left} is null.
     *
     * @return the constructed pair or an empty pair if {@code left} is null.
     */
    public static <L, R> Pair<L, R> ofLeft(L left) {
        if (left == null) {
            return empty();
        } else {
            return new Pair<>(left, null);
        }
    }

    /**
     * Constructs a pair with its right value being {@code right}, or returns an empty pair if
     * {@code right} is null.
     *
     * @return the constructed pair or an empty pair if {@code right} is null.
     */
    public static <L, R> Pair<L, R> ofRight(R right) {
        if (right == null) {
            return empty();
        } else {
            return new Pair<>(null, right);
        }
    }

    /**
     * Constructs a pair with its left value being {@code left}, and its right value being
     * {@code right}, or returns an empty pair if both inputs are null.
     *
     * @return the constructed pair or an empty pair if both inputs are null.
     */
    public static <L, R> Pair<L, R> of(L left, R right) {
        if (right == null && left == null) {
            return empty();
        } else {
            return new Pair<>(left, right);
        }
    }

    private Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Returns the left value of this pair.
     */
    public L getLeft() {
        return left;
    }

    /**
     * Returns the right value of this pair.
     */
    public R getRight() {
        return right;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(left) + 31 * Objects.hashCode(right);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) &&
               Objects.equals(right, pair.right);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("(%s, %s)", left, right);
    }
}

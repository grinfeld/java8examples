package com.mikerusoft.example.java8;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ImmutablePair<L, R> implements Serializable {

    private static final ImmutablePair<?,?> EMPTY = new ImmutablePair<>(null, null);

    public static <L, R> ImmutablePair<L, R> empty() {
        @SuppressWarnings("unchecked")
        ImmutablePair<L, R> empty = (ImmutablePair<L, R>)EMPTY;
        return empty;
    }

    public static <L,R> ImmutablePair<L, R> of(L left, R right) {
        return new ImmutablePair<>(left, right);
    }

    private ImmutablePair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    private final L left;
    private final R right;

    public L getLeft() {
        if (isEmpty())
            throw new NoSuchElementException("No left present");
        return left;
    }

    public R getRight() {
        if (isEmpty())
            throw new NoSuchElementException("No right present");
        return right;
    }

    public <L1, R1> ImmutablePair<L1, R1> flatMap(Function<ImmutablePair<L, R>, ImmutablePair<L1, R1>> mapper) {
        Objects.requireNonNull(mapper);
        return isEmpty() ? empty() : mapper.apply(this);
    }

    public <N> N map(Function<ImmutablePair<L,R>, N> mapper) {
        Objects.requireNonNull(mapper);
        return isEmpty() ? null : mapper.apply(this);
    }

    public <N> N map(Function<ImmutablePair<L,R>, N> mapper, Supplier<N> defSupplier) {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(defSupplier);
        return isEmpty() ? defSupplier.get() : mapper.apply(this);
    }

    public ImmutablePair<L, R> filter(Predicate<ImmutablePair<L, R>> predicate) {
        Objects.requireNonNull(predicate);
        return !isEmpty() && predicate.test(this) ? this : empty();
    }

    public L getLeftElse(L def) { return isEmpty() ? def : this.left; }
    public R getRightElse(R def) { return isEmpty() ? def : this.right; }

    public <E extends Throwable> L getLeftElseThrow(Supplier<E> supplier) throws RuntimeException {
        Objects.requireNonNull(supplier);
        if (isEmpty())
            throw prepareRuntimeException(supplier.get());
        return this.getLeft();
    }

    public <E extends Throwable> R getRightElseThrow(Supplier<E> supplier) throws RuntimeException {
        Objects.requireNonNull(supplier);
        if (isEmpty())
            throw prepareRuntimeException(supplier.get());
        return this.getRight();
    }

    public boolean isEmpty() {
        return EMPTY == this;
    }

    private RuntimeException prepareRuntimeException(Throwable throwable) {
        RuntimeException re = null;
        if (throwable instanceof RuntimeException)
            re = (RuntimeException)throwable;
        else
            re = new RuntimeException(throwable.getMessage(), throwable);
        return re;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutablePair<?, ?> pair = (ImmutablePair<?, ?>) o;
        if (pair.isEmpty() && this.isEmpty()) return true;
        if (pair.isEmpty() || this.isEmpty()) return false;
        return Objects.equals(left, pair.left) &&
                Objects.equals(right, pair.right);
    }
}

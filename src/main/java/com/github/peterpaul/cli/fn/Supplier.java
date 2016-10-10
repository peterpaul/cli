package com.github.peterpaul.cli.fn;

@FunctionalInterface
public interface Supplier<T> {
    T supply();

    static <T> Supplier<T> of(final T item) {
        return () -> item;
    }

    default Supplier<T> cache() {
        return new CachedSupplier<>(this);
    }

    default <R> Supplier<R> map(Function<T, R> f) {
        return () -> f.apply(supply());
    }
}

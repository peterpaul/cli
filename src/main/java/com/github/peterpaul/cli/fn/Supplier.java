package com.github.peterpaul.cli.fn;

@FunctionalInterface
public interface Supplier<T> extends java.util.function.Supplier<T> {
    static <T> Supplier<T> of(final T item) {
        return () -> item;
    }

    default Supplier<T> cache() {
        return new CachedSupplier<>(this);
    }
}

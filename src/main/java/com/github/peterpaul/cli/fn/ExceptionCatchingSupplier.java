package com.github.peterpaul.cli.fn;

import java.util.Optional;

public class ExceptionCatchingSupplier<T> implements Supplier<Optional<T>> {
    private final Supplier<T> supplier;

    private ExceptionCatchingSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> ExceptionCatchingSupplier<T> catchingFor(Supplier<T> supplier) {
        return new ExceptionCatchingSupplier<>(supplier);
    }

    @Override
    public Optional<T> get() {
        try {
            return Optional.of(supplier.get());
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }
}

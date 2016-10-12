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
    public Optional<T> supply() {
        try {
            return Optional.of(supplier.supply());
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }
}

package com.github.peterpaul.cli.fn;

import com.github.peterpaul.fn.Option;
import com.github.peterpaul.fn.Supplier;

public class ExceptionCatchingSupplier<T> extends Supplier<Option<T>> {
    private final Supplier<T> supplier;

    private ExceptionCatchingSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> ExceptionCatchingSupplier<T> catchingFor(Supplier<T> supplier) {
        return new ExceptionCatchingSupplier<>(supplier);
    }

    @Override
    public Option<T> get() {
        try {
            return Option.of(supplier.get());
        } catch (RuntimeException e) {
            return Option.none();
        }
    }
}

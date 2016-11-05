package com.github.peterpaul.cli.fn;

import java.util.function.Supplier;

public class Suppliers {
    public static <T> Supplier<T> cached(Supplier<T> in) {
        return new CachedSupplier<>(in);
    }
}

package com.github.peterpaul.cli.instantiator;

import java.util.function.Supplier;

import static com.github.peterpaul.cli.collection.ServiceLoaderStreamer.loadUniqueInstance;
import static com.github.peterpaul.cli.fn.Suppliers.cached;

public class InstantiatorSupplier implements Supplier<Instantiator> {
    private static final Supplier<Instantiator> INSTANTIATOR_SUPPLIER = cached(new InstantiatorSupplier());

    private InstantiatorSupplier() {
    }

    public static <T> T instantiate(Class<T> aClass) {
        return INSTANTIATOR_SUPPLIER.get().instantiate(aClass);
    }

    @Override
    public Instantiator get() {
        return loadUniqueInstance(Instantiator.class);
    }
}

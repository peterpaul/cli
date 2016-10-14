package com.github.peterpaul.cli.instantiator;

import com.github.peterpaul.cli.fn.Supplier;

import static com.github.peterpaul.cli.collection.ServiceLoaderStreamer.loadUniqueInstance;

public class InstantiatorSupplier implements Supplier<Instantiator> {
    private static final Supplier<Instantiator> INSTANTIATOR_SUPPLIER = new InstantiatorSupplier().cache();

    private InstantiatorSupplier() {
    }

    public static <T> T instantiate(Class<T> aClass) {
        return INSTANTIATOR_SUPPLIER.supply().instantiate(aClass);
    }

    @Override
    public Instantiator supply() {
        return loadUniqueInstance(Instantiator.class);
    }
}

package com.github.peterpaul.cli.instantiator;

import com.github.peterpaul.cli.fn.Supplier;

import static com.github.peterpaul.cli.collection.ServiceLoaderStreamer.loadUniqueInstance;

public class InstantiatorSupplier implements Supplier<Instantiator> {
    public static final Supplier<Instantiator> INSTANTIATOR_SUPPLIER = new InstantiatorSupplier().cache();

    private InstantiatorSupplier() {
    }

    @Override
    public Instantiator supply() {
        return loadUniqueInstance(Instantiator.class);
    }
}

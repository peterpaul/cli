package com.github.peterpaul.cli.instantiator;

import com.github.peterpaul.fn.Function;
import com.github.peterpaul.fn.Supplier;

import static com.github.peterpaul.cli.collection.ServiceLoaderStreamer.tryLoadUniqueInstance;

public class InstantiatorSupplier extends Supplier<Instantiator> {
    private static final Supplier<Instantiator> INSTANTIATOR_SUPPLIER = new InstantiatorSupplier().cache();

    private InstantiatorSupplier() {
    }

    public static Function<Class, Object> instantiate() {
        return new Function<Class, Object>() {
            @Override
            public Object apply(Class tClass) {
                return INSTANTIATOR_SUPPLIER.get().instantiate(tClass);
            }
        };
    }

    public static <T> T instantiate(Class<T> aClass) {
        return INSTANTIATOR_SUPPLIER.get().instantiate(aClass);
    }

    @Override
    public Instantiator get() {
        return tryLoadUniqueInstance(Instantiator.class)
                .or(new DefaultConstructorInstantiator());
    }
}

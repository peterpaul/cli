package com.github.peterpaul.cli.instantiator;

import com.github.peterpaul.cli.fn.ExceptionCatchingSupplier;
import com.github.peterpaul.cli.fn.Supplier;

public class ByServiceLoaderOrNewClassInstanceInstantiator implements Instantiator {
    private final ClassInstantiator classInstantiator = new ClassInstantiator();
    private final ServiceLoaderInstantiator serviceLoaderInstantiator = new ServiceLoaderInstantiator();

    @Override
    public <T> T instantiate(Class<T> aClass) {
        return ExceptionCatchingSupplier.catchingFor(new Supplier<T>() {
            @Override
            public T supply() {
                return serviceLoaderInstantiator.instantiate(aClass);
            }
        }).supply().orElseGet(new java.util.function.Supplier<T>() {
            @Override
            public T get() {
                return classInstantiator.instantiate(aClass);
            }
        });
    }
}

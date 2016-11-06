package com.github.peterpaul.cli.instantiator;

import com.github.peterpaul.fn.Supplier;

import static com.github.peterpaul.cli.fn.ExceptionCatchingSupplier.catchingFor;

public class ByServiceLoaderOrNewClassInstanceInstantiator implements Instantiator {
    private final ClassInstantiator classInstantiator = new ClassInstantiator();
    private final ServiceLoaderInstantiator serviceLoaderInstantiator = new ServiceLoaderInstantiator();

    @Override
    public <T> T instantiate(final Class<T> aClass) {
        return catchingFor(new Supplier<T>() {
            @Override
            public T get() {
                return serviceLoaderInstantiator.instantiate(aClass);
            }
        })
                .get()
                .or(new Supplier<T>() {
                    @Override
                    public T get() {
                        return classInstantiator.instantiate(aClass);
                    }
                });
    }
}

package com.github.peterpaul.cli.instantiator;

import com.github.peterpaul.fn.Supplier;

import javax.annotation.Nonnull;

public class ByServiceLoaderOrDefaultConstructorInstantiator implements Instantiator {
    private final DefaultConstructorInstantiator defaultConstructorInstantiator = new DefaultConstructorInstantiator();
    private final ServiceLoaderInstantiator serviceLoaderInstantiator = new ServiceLoaderInstantiator();

    @Override
    public <T> T instantiate(final Class<T> aClass) {
        return new Supplier<T>() {
            @Nonnull
            @Override
            public T get() {
                return serviceLoaderInstantiator.instantiate(aClass);
            }
        }
                .orWhenRuntimeException(new Supplier<T>() {
                    @Nonnull
                    @Override
                    public T get() {
                        return defaultConstructorInstantiator.instantiate(aClass);
                    }
                })
                .get();
    }
}

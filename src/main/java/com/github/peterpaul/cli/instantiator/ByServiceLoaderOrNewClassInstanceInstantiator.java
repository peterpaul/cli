package com.github.peterpaul.cli.instantiator;

import static com.github.peterpaul.cli.fn.ExceptionCatchingSupplier.catchingFor;

public class ByServiceLoaderOrNewClassInstanceInstantiator implements Instantiator {
    private final ClassInstantiator classInstantiator = new ClassInstantiator();
    private final ServiceLoaderInstantiator serviceLoaderInstantiator = new ServiceLoaderInstantiator();

    @Override
    public <T> T instantiate(Class<T> aClass) {
        return catchingFor(() -> serviceLoaderInstantiator.instantiate(aClass))
                .get()
                .orElseGet(() -> classInstantiator.instantiate(aClass));
    }
}

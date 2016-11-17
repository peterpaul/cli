package net.kleinhaneveld.cli.instantiator;

import net.kleinhaneveld.cli.collection.ServiceLoaderStreamer;

public class ServiceLoaderInstantiator implements Instantiator {
    @Override
    public <T> T instantiate(Class<T> aClass) {
        return ServiceLoaderStreamer.loadUniqueInstance(aClass);
    }
}

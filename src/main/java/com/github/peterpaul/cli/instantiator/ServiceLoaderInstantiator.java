package com.github.peterpaul.cli.instantiator;

import com.github.peterpaul.cli.collection.ServiceLoaderStreamer;

public class ServiceLoaderInstantiator implements Instantiator {
    @Override
    public <T> T instantiate(Class<T> aClass) {
        return ServiceLoaderStreamer.loadUniqueInstance(aClass);
    }
}

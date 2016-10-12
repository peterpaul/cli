package com.github.peterpaul.cli.instantiator;

public class ClassInstantiator implements Instantiator {
    @Override
    public <T> T instantiate(Class<T> aClass) {
        try {
            return aClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

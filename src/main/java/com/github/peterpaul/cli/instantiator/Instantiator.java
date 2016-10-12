package com.github.peterpaul.cli.instantiator;

public interface Instantiator {
    <T> T instantiate(Class<T> aClass);
}

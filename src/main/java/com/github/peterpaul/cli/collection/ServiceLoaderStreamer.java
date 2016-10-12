package com.github.peterpaul.cli.collection;

import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class ServiceLoaderStreamer {
    public static <T> Stream<T> stream(Class<T> aClass) {
        return StreamSupport.stream(ServiceLoader.load(aClass).spliterator(), false);
    }

    /**
     * @param aClass Class to get instance of.
     * @param <T>    Type of Class to get instance of.
     * @return An unique instance loaded using {@link ServiceLoader}.
     * @throws com.github.peterpaul.cli.exceptions.NoElementsException     If no instances are available.
     * @throws com.github.peterpaul.cli.exceptions.TooManyElementException If multiple instances are available.
     */
    public static <T> T loadUniqueInstance(Class<T> aClass) {
        return CollectionUtil.getUnique(stream(aClass)
                .collect(Collectors.toSet()));
    }
}

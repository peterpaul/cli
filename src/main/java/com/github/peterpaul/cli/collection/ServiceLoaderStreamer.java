package com.github.peterpaul.cli.collection;

import com.github.peterpaul.cli.exceptions.NoElementsException;
import com.github.peterpaul.cli.exceptions.TooManyElementException;
import com.github.peterpaul.fn.Function;
import com.github.peterpaul.fn.Stream;
import com.github.peterpaul.fn.status.NoElements;
import com.github.peterpaul.fn.status.TooManyElements;
import com.github.peterpaul.fn.status.UniquenessErrorStatus;

import java.util.ServiceLoader;

public abstract class ServiceLoaderStreamer {
    public static <T> Stream<T> stream(Class<T> aClass) {
        return Stream.stream(ServiceLoader.load(aClass));
    }

    /**
     * @param aClass Class to get instance of.
     * @param <T>    Type of Class to get instance of.
     * @return An unique instance loaded using {@link ServiceLoader}.
     * @throws com.github.peterpaul.cli.exceptions.NoElementsException     If no instances are available.
     * @throws com.github.peterpaul.cli.exceptions.TooManyElementException If multiple instances are available.
     */
    public static <T> T loadUniqueInstance(Class<T> aClass) {
        return stream(aClass).unique().map(
                Function.<T>identity(),
                new Function<UniquenessErrorStatus<T>, T>() {
                    @Override
                    public T apply(UniquenessErrorStatus<T> errorStatus) {
                        return errorStatus.getStatus().map(
                                new Function<NoElements, T>() {
                                    @Override
                                    public T apply(NoElements noElements) {
                                        throw new NoElementsException();
                                    }
                                },
                                new Function<TooManyElements<T>, T>() {
                                    @Override
                                    public T apply(TooManyElements<T> tooManyElements) {
                                        throw new TooManyElementException(tooManyElements.getItems());
                                    }
                                });
                    }
                });
    }
}

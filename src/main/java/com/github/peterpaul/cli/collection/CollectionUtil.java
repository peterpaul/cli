package com.github.peterpaul.cli.collection;

import com.github.peterpaul.cli.exceptions.NoElementsException;
import com.github.peterpaul.cli.exceptions.TooManyElementException;

import java.util.Collection;
import java.util.Optional;

public class CollectionUtil {
    public static <T> T getUnique(Collection<T> list) {
        if (list.isEmpty()) {
            throw new NoElementsException();
        } else if (list.size() == 1) {
            return list.iterator().next();
        } else {
            throw new TooManyElementException();
        }
    }

    public static <T> Optional<T> tryGetUnique(Collection<T> list) {
        if (list.isEmpty()) {
            return Optional.empty();
        } else if (list.size() == 1) {
            return Optional.of(list.iterator().next());
        } else {
            throw new TooManyElementException();
        }
    }
}

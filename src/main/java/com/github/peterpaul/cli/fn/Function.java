package com.github.peterpaul.cli.fn;

import java.util.Map;
import java.util.Optional;

@FunctionalInterface
public interface Function<T, R> {
    static <T, R> Function<T, Optional<R>> mapper(Map<T, R> map) {
        return key -> Optional.ofNullable(map.get(key));
    }

    R apply(T input);

    default <S> Function<T, S> $(Function<R, S> g) {
        return (input) -> g.apply(Function.this.apply(input));
    }
}

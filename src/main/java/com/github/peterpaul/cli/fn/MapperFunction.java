package com.github.peterpaul.cli.fn;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class MapperFunction {
    public static <T, R> Function<T, Optional<R>> mapper(Map<T, R> map) {
        return key -> Optional.ofNullable(map.get(key));
    }
}

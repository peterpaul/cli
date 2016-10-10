package com.github.peterpaul.cli.fn;

@FunctionalInterface
public interface Function<T, R> {
    R apply(T input);

    default <S> Function<T, S> $(Function<R, S> g) {
        return (input) -> g.apply(Function.this.apply(input));
    }
}

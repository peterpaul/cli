package com.github.peterpaul.cli.exceptions;

import java.util.Set;

public class TooManyElementException extends RuntimeException {
    private final Set items;

    public <T> TooManyElementException(Set<T> items) {
        this.items = items;
    }

    public Set getItems() {
        return items;
    }
}

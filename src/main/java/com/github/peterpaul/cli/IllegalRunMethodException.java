package com.github.peterpaul.cli;

public class IllegalRunMethodException extends RuntimeException {
    public IllegalRunMethodException(String message) {
        super(message);
    }

    public IllegalRunMethodException(String message, Throwable cause) {
        super(message, cause);
    }
}

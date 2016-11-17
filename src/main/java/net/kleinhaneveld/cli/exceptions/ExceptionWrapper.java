package net.kleinhaneveld.cli.exceptions;

public abstract class ExceptionWrapper {
    public static RuntimeException wrap(Throwable t) {
        if (t instanceof RuntimeException) {
            return (RuntimeException) t;
        } else {
            return new RuntimeException(t);
        }
    }
}

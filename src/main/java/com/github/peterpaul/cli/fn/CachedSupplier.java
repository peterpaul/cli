package com.github.peterpaul.cli.fn;

public class CachedSupplier<T> implements Supplier<T>, Registerable {
    private final Supplier<T> delegate;
    private final Broadcaster broadcaster = new Broadcaster();
    private volatile T value;

    CachedSupplier(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T supply() {
        T result = value;
        if (result == null) {
            synchronized (this) {
                result = value;
                if (result == null) {
                    value = result = delegate.supply();
                }
            }
        }
        return result;
    }

    public void invalidate() {
        value = null;
        broadcaster.run();
    }

    @Override
    public void register(Runner listener) {
        broadcaster.register(listener);
    }

    @Override
    public void unregister(Runner listener) {
        broadcaster.unregister(listener);
    }
}

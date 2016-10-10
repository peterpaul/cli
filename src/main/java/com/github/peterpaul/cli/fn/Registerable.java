package com.github.peterpaul.cli.fn;

public interface Registerable {
    void register(Runner listener);
    void unregister(Runner listener);
}

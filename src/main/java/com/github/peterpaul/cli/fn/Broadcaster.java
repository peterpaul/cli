package com.github.peterpaul.cli.fn;

import java.util.LinkedHashSet;
import java.util.Set;

public class Broadcaster implements Runner, Registerable {
    private final Set<Runner> listeners = new LinkedHashSet<>();

    @Override
    public void run() {
        listeners.forEach(Runner::run);
    }

    @Override
    public void register(Runner listener) {
        listeners.add(listener);
    }

    @Override
    public void unregister(Runner listener) {
        listeners.remove(listener);
    }
}

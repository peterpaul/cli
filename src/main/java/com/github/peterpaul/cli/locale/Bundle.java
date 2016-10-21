package com.github.peterpaul.cli.locale;

import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;

public class Bundle implements Function<String, String> {
    private final Optional<ResourceBundle> resourceBundle;

    public Bundle(Optional<ResourceBundle> resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Override
    public String apply(String s) {
        return resourceBundle
                .map(r -> r.containsKey(s)
                        ? r.getString(s)
                        : s)
                .orElse(s);
    }
}

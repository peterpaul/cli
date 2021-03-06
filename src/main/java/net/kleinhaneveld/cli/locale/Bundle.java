package net.kleinhaneveld.cli.locale;

import net.kleinhaneveld.fn.Function;
import net.kleinhaneveld.fn.Option;

import java.util.ResourceBundle;

public class Bundle extends Function<String, String> {
    private final Option<ResourceBundle> resourceBundle;

    public Bundle(Option<ResourceBundle> resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Override
    public String apply(final String s) {
        return resourceBundle
                .map(new Function<ResourceBundle, String>() {
                    @Override
                    public String apply(ResourceBundle r) {
                        return r.containsKey(s)
                                ? r.getString(s)
                                : s;
                    }
                })
                .or(s);
    }
}

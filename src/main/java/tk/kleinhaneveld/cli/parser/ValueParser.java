package tk.kleinhaneveld.cli.parser;

import tk.kleinhaneveld.cli.ValueParseException;

public interface ValueParser<T> {
    Class<T> getSupportedClass();

    T parse(String argument) throws ValueParseException;
}

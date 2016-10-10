package tk.kleinhaneveld.cli.parser;

import tk.kleinhaneveld.cli.ValueParseException;

public interface ValueParser<T> {
    Class[] getSupportedClasses();

    T parse(String argument) throws ValueParseException;
}

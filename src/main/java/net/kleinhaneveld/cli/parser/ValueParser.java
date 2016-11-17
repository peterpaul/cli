package net.kleinhaneveld.cli.parser;

import net.kleinhaneveld.cli.exceptions.ValueParseException;

public interface ValueParser<T> {
    Class[] getSupportedClasses();

    T parse(String argument) throws ValueParseException;
}

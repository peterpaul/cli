package net.kleinhaneveld.cli.parser;

import net.kleinhaneveld.cli.exceptions.ValueParseException;

public class StringValueParser implements ValueParser<String> {
    public Class[] getSupportedClasses() {
        return new Class[]{String.class};
    }

    public String parse(String argument) throws ValueParseException {
        return argument;
    }
}

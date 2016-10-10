package com.github.peterpaul.cli.parser;

import com.github.peterpaul.cli.ValueParseException;

public class StringValueParser implements ValueParser<String> {
    public Class[] getSupportedClasses() {
        return new Class[]{String.class};
    }

    public String parse(String argument) throws ValueParseException {
        return argument;
    }
}

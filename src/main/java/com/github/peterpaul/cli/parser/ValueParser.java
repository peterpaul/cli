package com.github.peterpaul.cli.parser;

import com.github.peterpaul.cli.exceptions.ValueParseException;

public interface ValueParser<T> {
    Class[] getSupportedClasses();

    T parse(String argument) throws ValueParseException;
}

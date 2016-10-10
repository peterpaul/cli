package com.github.peterpaul.cli.parser;

import com.github.peterpaul.cli.ValueParseException;

public class IntValueParser implements ValueParser<Integer> {
    @Override
    public Class[] getSupportedClasses() {
        return new Class[]{Integer.class, int.class};
    }

    @Override
    public Integer parse(String argument) throws ValueParseException {
        return Integer.parseInt(argument);
    }
}

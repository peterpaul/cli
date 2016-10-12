package com.github.peterpaul.cli.parser;

import com.github.peterpaul.cli.exceptions.ValueParseException;

public class BooleanValueParser implements ValueParser<Boolean> {
    public Class[] getSupportedClasses() {
        return new Class[]{Boolean.class, boolean.class};
    }

    public Boolean parse(String argument) throws ValueParseException {
        return "".equals(argument) || Boolean.parseBoolean(argument);
    }
}

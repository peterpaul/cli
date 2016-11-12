package com.github.peterpaul.cli.examples;

import com.github.peterpaul.cli.exceptions.ValueParseException;
import com.github.peterpaul.cli.parser.ValueParser;

public class MyTypeParser implements ValueParser<MyType> {
    @Override
    public Class[] getSupportedClasses() {
        return new Class[]{MyType.class};
    }

    @Override
    public MyType parse(String argument) throws ValueParseException {
        return new MyType(argument);
    }
}

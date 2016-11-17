package net.kleinhaneveld.cli.examples;

import net.kleinhaneveld.cli.exceptions.ValueParseException;
import net.kleinhaneveld.cli.parser.ValueParser;

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

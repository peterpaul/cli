package net.kleinhaneveld.cli.parser;

import net.kleinhaneveld.cli.exceptions.ValueParseException;

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

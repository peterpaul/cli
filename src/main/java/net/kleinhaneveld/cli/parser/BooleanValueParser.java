package net.kleinhaneveld.cli.parser;

import net.kleinhaneveld.cli.exceptions.ValueParseException;

public class BooleanValueParser implements ValueParser<Boolean> {
    public Class[] getSupportedClasses() {
        return new Class[]{Boolean.class, boolean.class};
    }

    public Boolean parse(String argument) throws ValueParseException {
        return "".equals(argument) || Boolean.parseBoolean(argument);
    }
}

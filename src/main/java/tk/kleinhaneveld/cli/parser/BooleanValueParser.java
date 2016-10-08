package tk.kleinhaneveld.cli.parser;

import tk.kleinhaneveld.cli.ValueParseException;

public class BooleanValueParser implements ValueParser<Boolean> {
    public Class<Boolean> getSupportedClass() {
        return Boolean.class;
    }

    public Boolean parse(String argument) throws ValueParseException {
        return Boolean.parseBoolean(argument);
    }
}

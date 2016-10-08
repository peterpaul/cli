package tk.kleinhaneveld.cli.parser;

import tk.kleinhaneveld.cli.ValueParseException;

public class StringValueParser implements ValueParser<String> {
    public Class<String> getSupportedClass() {
        return String.class;
    }

    public String parse(String argument) throws ValueParseException {
        return argument;
    }
}

package tk.kleinhaneveld.cli.parser;

import tk.kleinhaneveld.cli.ValueParseException;

public class StringValueParser implements ValueParser<String> {
    public Class[] getSupportedClasses() {
        return new Class[]{String.class};
    }

    public String parse(String argument) throws ValueParseException {
        return argument;
    }
}

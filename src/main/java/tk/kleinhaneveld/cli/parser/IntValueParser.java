package tk.kleinhaneveld.cli.parser;

import tk.kleinhaneveld.cli.ValueParseException;

/**
 * Created by peterpaul on 7-10-16.
 */
public class IntValueParser implements ValueParser<Integer> {
    @Override
    public Class<Integer> getSupportedClass() {
        return int.class;
    }

    @Override
    public Integer parse(String argument) throws ValueParseException {
        return Integer.parseInt(argument);
    }
}

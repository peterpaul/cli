package tk.kleinhaneveld.cli;

import tk.kleinhaneveld.cli.parser.ValueParser;

public interface ValueParserProvider {
    <T> ValueParser<T> get(Class<T> valueClass);
}

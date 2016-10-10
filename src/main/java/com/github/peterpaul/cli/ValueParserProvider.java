package com.github.peterpaul.cli;

import com.github.peterpaul.cli.parser.ValueParser;

public interface ValueParserProvider {
    <T> ValueParser<T> get(Class<T> valueClass);
}

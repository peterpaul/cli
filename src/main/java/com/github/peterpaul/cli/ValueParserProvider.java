package com.github.peterpaul.cli;

import com.github.peterpaul.cli.fn.Pair;
import com.github.peterpaul.cli.parser.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValueParserProvider {
    private final Map<Class, ValueParser> valueParserMap;

    public ValueParserProvider() {
        valueParserMap = Stream.of(
                new BooleanValueParser(),
                new FileValueParser(),
                new IntValueParser(),
                new StringValueParser(),
                new UrlValueParser())
                .flatMap(p -> Arrays.stream(p.getSupportedClasses()).map(c -> Pair.of(c, p)))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    public ValueParser getValueParser(Field field, Class<? extends ValueParser> parserClass) {
        ValueParser valueParser;
        if (parserClass != ValueParser.class) {
            try {
                valueParser = parserClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            valueParser = valueParserMap.get(field.getType());
            if (valueParser == null) {
                throw new ValueParseException("No ValueParser registered for: " + field.getType().getCanonicalName());
            }
        }
        return valueParser;
    }
}

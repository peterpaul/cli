package com.github.peterpaul.cli;

import com.github.peterpaul.cli.exceptions.ValueParseException;
import com.github.peterpaul.cli.fn.Pair;
import com.github.peterpaul.cli.parser.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.peterpaul.cli.instantiator.InstantiatorSupplier.INSTANTIATOR_SUPPLIER;

public class ValueParserProvider {
    private static final Map<Class, ValueParser> valueParserMap;

    static {
        valueParserMap = Stream.of(
                new BooleanValueParser(),
                new FileValueParser(),
                new IntValueParser(),
                new StringValueParser(),
                new UrlValueParser())
                .flatMap(p -> Arrays.stream(p.getSupportedClasses()).map(c -> Pair.of(c, p)))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    public static ValueParser getValueParser(Field field, Class<? extends ValueParser> parserClass) {
        ValueParser valueParser;
        if (parserClass != ValueParser.class) {
            valueParser = INSTANTIATOR_SUPPLIER.supply().instantiate(parserClass);
        } else {
            valueParser = valueParserMap.get(field.getType());
            if (valueParser == null) {
                throw new ValueParseException("No ValueParser registered for: '" + field.getDeclaringClass().getCanonicalName() + "::" + field.getName()+ ": "  + field.getType().getCanonicalName() +  "', please specify one using the 'parser' attribute of the Cli annnotation.");
            }
        }
        return valueParser;
    }
}

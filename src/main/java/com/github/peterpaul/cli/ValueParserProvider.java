package com.github.peterpaul.cli;

import com.github.peterpaul.cli.collection.ServiceLoaderStreamer;
import com.github.peterpaul.cli.exceptions.ValueParseException;
import com.github.peterpaul.cli.fn.Pair;
import com.github.peterpaul.cli.parser.ValueParser;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.github.peterpaul.cli.fn.Suppliers.cached;
import static com.github.peterpaul.cli.instantiator.InstantiatorSupplier.instantiate;

public class ValueParserProvider {
    private static final Supplier<Map<Class, ValueParser>> VALUE_PARSER_MAP_SUPPLIER = cached(
            () -> ServiceLoaderStreamer.stream(ValueParser.class)
                    .flatMap(p -> Arrays.stream(p.getSupportedClasses()).map(c -> Pair.of(c, p)))
                    .collect(getPairMapCollector()));

    private static Collector<Pair<Class, ValueParser>, ?, Map<Class, ValueParser>> getPairMapCollector() {
        return Collectors.toMap(Pair::getLeft, Pair::getRight);
    }

    public static ValueParser getValueParser(Field field, Class<? extends ValueParser> parserClass) {
        ValueParser valueParser;
        if (parserClass != ValueParser.class) {
            valueParser = instantiate(parserClass);
        } else {
            valueParser = VALUE_PARSER_MAP_SUPPLIER.get().get(field.getType());
            if (valueParser == null) {
                throw new ValueParseException("No ValueParser registered for: '" + field.getDeclaringClass().getCanonicalName() + "::" + field.getName() + ": " + field.getType().getCanonicalName() + "', please specify one using the 'parser' attribute of the Cli annnotation.");
            }
        }
        return valueParser;
    }
}

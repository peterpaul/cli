package com.github.peterpaul.cli;

import com.github.peterpaul.cli.collection.ServiceLoaderStreamer;
import com.github.peterpaul.cli.exceptions.ValueParseException;
import com.github.peterpaul.cli.parser.ValueParser;
import com.github.peterpaul.fn.*;

import java.lang.reflect.Field;
import java.util.Map;

import static com.github.peterpaul.cli.instantiator.InstantiatorSupplier.instantiate;

public abstract class ValueParserProvider {
    private static final Supplier<Map<Class, ValueParser>> VALUE_PARSER_MAP_SUPPLIER = new Supplier<Map<Class, ValueParser>>() {
        @Override
        public Map<Class, ValueParser> get() {
            return ServiceLoaderStreamer.stream(ValueParser.class)
                    .flatMap(new Function<ValueParser, Recitable<Pair<Class, ValueParser>>>() {
                        @Override
                        public Recitable<Pair<Class, ValueParser>> apply(final ValueParser p) {
                            return Stream.stream(p.getSupportedClasses())
                                    .map(new Function<Class, Pair<Class, ValueParser>>() {
                                        @Override
                                        public Pair<Class, ValueParser> apply(Class c) {
                                            return Pair.pair(c, p);
                                        }
                                    });
                        }
                    })
                    .toMap(Function.<Pair<Class, ValueParser>>identity());
        }
    }.cache();

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

package com.github.peterpaul.cli;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ArgumentParserMatcher {
    private final ValueParserProvider valueParserProvider;

    public ArgumentParserMatcher(ValueParserProvider valueParserProvider) {
        this.valueParserProvider = valueParserProvider;
    }

    public boolean argumentParserDoesNotMatchFieldType(Field field, Cli.Argument argumentAnnotation) {
        return !Arrays.asList(valueParserProvider.getValueParser(field, argumentAnnotation.parser()).getSupportedClasses())
                .contains(field.getType());
    }
}

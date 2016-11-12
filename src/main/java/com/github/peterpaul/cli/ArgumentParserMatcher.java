package com.github.peterpaul.cli;

import java.lang.reflect.Field;
import java.util.Arrays;

public abstract class ArgumentParserMatcher {
    public static boolean argumentParserDoesNotMatchFieldType(Field field, Cli.Argument argumentAnnotation) {
        return !Arrays.asList(ValueParserProvider.getValueParser(field, argumentAnnotation.parser()).getSupportedClasses())
                .contains(field.getType());
    }
}

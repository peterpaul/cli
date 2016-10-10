package com.github.peterpaul.cli;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FieldsProvider {
    public static List<Field> getArgumentList(Class<?> commandClass) {
        return getArgumentStream(commandClass)
                .collect(Collectors.toList());
    }

    public static Stream<Field> getArgumentStream(Class<?> commandClass) {
        return getFieldStream(commandClass, Cli.Argument.class);
    }

    public static Stream<Field> getOptionStream(Class<?> commandClass) {
        return getFieldStream(commandClass, Cli.Option.class);
    }

    public static String getName(Field field, String name) {
        return Stream.of(name, field.getName())
                .filter(n -> n != null && !Objects.equals(n, ""))
                .findFirst()
                .get();
    }

    private static Stream<Field> getFieldStream(Class<?> commandClass, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(commandClass.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(annotationClass));
    }
}

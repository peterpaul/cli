package com.github.peterpaul.cli;

import com.github.peterpaul.cli.collection.ListUtil;
import com.github.peterpaul.cli.collection.TooManyElementException;
import com.github.peterpaul.cli.parser.ValueParser;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProgramRunner {
    private final ValueParserProvider valueParserProvider = new ValueParserProvider();

    public void run(String[] arguments, Object command) {
        parseOptions(arguments, command);
        parseArguments(arguments, command);

        Optional<Method> runMethod = Stream.of(getRunMethodByAnnotation(command), getRunMethodByName(command))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
        runMethod.ifPresent(method -> {
            if (method.getParameterCount() != 0) {
                throw new IllegalRunMethodException("Run method should not take any arguments, however '" + method.getName() + "' has '" + method.getParameterCount() + "'");
            }
            if (!method.getReturnType().equals(Void.TYPE)) {
                throw new IllegalRunMethodException("Run method should not return anything, however '" + method.getName() + "' returns '" + method.getReturnType().getCanonicalName() + "'");
            }
        });
        if (runMethod.isPresent()) {
            try {
                runMethod.get().invoke(command);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalRunMethodException("No run method found.");
        }
    }

    private Optional<Method> getRunMethodByName(Object command) {
        Class<?> commandClass = command.getClass();
        try {
            Method method = commandClass.getMethod("run");
            return Optional.of(method);
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    private Optional<Method> getRunMethodByAnnotation(Object command) {
        Class<?> commandClass = command.getClass();
        List<Method> annotatedRunMethods = Arrays.stream(commandClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Cli.Run.class))
                .collect(Collectors.toList());
        try {
            return ListUtil.tryGetUnique(annotatedRunMethods);
        } catch (TooManyElementException e) {
            throw new IllegalRunMethodException("Only one method may be annotated with " + Cli.Run.class.getCanonicalName(),
                    e);
        }
    }

    private void parseArguments(String[] arguments, Object command) {
        List<String> argumentList = Arrays.stream(arguments)
                .filter(s -> !s.startsWith("-"))
                .collect(Collectors.toList());
        handleArguments(command, argumentList);
    }

    private void parseOptions(String[] arguments, Object command) {
        Map<String, String> optionMap = Arrays.stream(arguments)
                .filter(s -> s.startsWith("-"))
                .collect(Collectors.toMap(ActualOptionParser::optionKey, ActualOptionParser::optionValue));
        handleOptions(command, optionMap);
    }

    private void handleArguments(Object command, List<String> argumentList) {
        Class<?> commandClass = command.getClass();
        List<Field> declaredArgumentList = Arrays.stream(commandClass.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Cli.Argument.class))
                .collect(Collectors.toList());
        for (int i = 0; i < declaredArgumentList.size(); i++) {
            Field field = declaredArgumentList.get(i);
            Cli.Argument argumentAnnotation = field.getAnnotation(Cli.Argument.class);
            if (isLastArgument(declaredArgumentList, i)
                    && argumentParserDoesNotMatchFieldType(field, argumentAnnotation)) {
                List<Object> value = argumentList.stream()
                        .map(a -> parseValue(field, a, argumentAnnotation.parser()))
                        .collect(Collectors.toList());
                argumentList.clear();
                setFieldValue(command, field, value);
            } else {
                String value = argumentList.remove(0);
                Object parsedValue = parseValue(field, value, argumentAnnotation.parser());
                setFieldValue(command, field, parsedValue);
            }
        }
        if (!argumentList.isEmpty()) {
            throw new ValueParseException("Received unhandled arguments: " + argumentList);
        }
    }

    private boolean argumentParserDoesNotMatchFieldType(Field field, Cli.Argument argumentAnnotation) {
        return !Arrays.asList(valueParserProvider.getValueParser(field, argumentAnnotation.parser()).getSupportedClasses())
                .contains(field.getType());
    }

    private static boolean isLastArgument(List<Field> declaredArgumentList, int i) {
        return i == declaredArgumentList.size() - 1;
    }

    private void handleOptions(Object command, Map<String, String> optionMap) {
        Class<?> commandClass = command.getClass();
        Arrays.stream(commandClass.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Cli.Option.class))
                .forEach((field) -> {
                    Cli.Option optionAnnotation = field.getAnnotation(Cli.Option.class);
                    Optional<String> value = getOptionValue(optionMap, field);
                    if (value.isPresent()) {
                        Object parsedValue = parseValue(field, value.get(), optionAnnotation.parser());
                        setFieldValue(command, field, parsedValue);
                    }
                });
    }

    private static void setFieldValue(Object command, Field field, Object parsedValue) {
        try {
            field.setAccessible(true);
            field.set(command, parsedValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object parseValue(Field field, String value, Class<? extends ValueParser> valueParserClass) {
        ValueParser valueParser = valueParserProvider.getValueParser(field, valueParserClass);
        return valueParser.parse(value);
    }


    private static Optional<String> getOptionValue(Map<String, String> optionMap, Field field) {
        Cli.Option optionAnnotation = field.getAnnotation(Cli.Option.class);
        String name = getName(field, optionAnnotation.name());
        return Stream.of(
                "--" + name,
                "-" + optionAnnotation.shortName())

                .filter(n -> n != null)
                .map(optionMap::get)
                .filter(n -> n != null)
                .findFirst();
    }

    private static String getName(Field field, String name) {
        return Stream.of(name, field.getName())
                .filter(n -> n != null && !Objects.equals(n, ""))
                .findFirst()
                .get();
    }
}

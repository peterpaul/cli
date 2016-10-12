package com.github.peterpaul.cli;

import com.github.peterpaul.cli.exceptions.ValueParseException;
import com.github.peterpaul.cli.parser.ValueParser;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.peterpaul.cli.instantiator.InstantiatorSupplier.INSTANTIATOR_SUPPLIER;

public class ProgramRunner {
    public static void run(Object command, String[] arguments) {
        run(command, getArgumentList(arguments), getOptionMap(arguments));
    }

    public static void run(Object command, List<String> argumentList, Map<String, String> optionMap) {
        try {
            Cli.Command commandAnnotation = AnnotationHelper.getCommandAnnotation(command);
            if (commandAnnotation.subCommands().length == 0) {
                runCommand(command, argumentList, optionMap);
            } else {
                runCompositeCommand(command, argumentList, optionMap);
            }
        } catch (ValueParseException e) {
            System.err.println(HelpGenerator.generateHelp(command, e.getMessage()));
        }
    }

    private static void runCommand(Object command, List<String> argumentList, Map<String, String> optionMap) {
        handleOptions(command, optionMap);
        handleArguments(command, argumentList);
        CommandRunner.runCommand(command);
    }

    public static String getCommandName(Class aClass) {
        return AnnotationHelper.getCommandAnnotation(aClass).name();
    }

    public static Class getCommandClass(Class aClass) {
        return aClass;
    }

    private static void runCompositeCommand(Object command, List<String> argumentList, Map<String, String> optionMap) {
        handleOptions(command, optionMap);
        Cli.Command commandAnnotation = AnnotationHelper.getCommandAnnotation(command);
        Map<String, Class> subCommandMap = Arrays.stream(commandAnnotation.subCommands())
                .collect(Collectors.toMap(ProgramRunner::getCommandName, ProgramRunner::getCommandClass));
        String subCommandArgument = argumentList.remove(0);
        Class subCommandClass = subCommandMap.get(subCommandArgument);
        if (subCommandClass == null) {
            throw new ValueParseException("Not a subcommand: " + subCommandArgument + " allowed are " + subCommandMap.entrySet());
        } else {
            Object subCommand = INSTANTIATOR_SUPPLIER.supply().instantiate(subCommandClass);
            run(subCommand, argumentList, optionMap);
        }
    }

    private static void setFieldValue(Object command, Field field, Object parsedValue) {
        try {
            field.setAccessible(true);
            field.set(command, parsedValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Optional<String> getOptionValue(Map<String, String> optionMap, Field field) {
        Cli.Option optionAnnotation = AnnotationHelper.getOptionAnnotation(field);
        String name = FieldsProvider.getName(field, optionAnnotation.name());
        Optional<String> valueFromCommandLine = Stream.of(
                "--" + name,
                "-" + optionAnnotation.shortName())

                .filter(n -> n != null)
                .map(optionMap::get)
                .filter(n -> n != null)
                .findFirst();
        if (valueFromCommandLine.isPresent()) {
            return valueFromCommandLine;
        } else {
            return AnnotationHelper.fromEmpty(optionAnnotation.defaultValue());
        }
    }

    private static List<String> getArgumentList(String[] arguments) {
        return Arrays.stream(arguments)
                .filter(s -> !s.startsWith("-"))
                .collect(Collectors.toList());
    }

    private static Map<String, String> getOptionMap(String[] arguments) {
        return Arrays.stream(arguments)
                .filter(s -> s.startsWith("-"))
                .collect(Collectors.toMap(ActualOptionParser::optionKey, ActualOptionParser::optionValue));
    }

    private static void handleArguments(Object command, List<String> argumentList) {
        Class<?> commandClass = command.getClass();
        List<Field> declaredArgumentList = FieldsProvider.getArgumentList(commandClass);
        for (int i = 0; i < declaredArgumentList.size(); i++) {
            Field field = declaredArgumentList.get(i);
            Cli.Argument argumentAnnotation = AnnotationHelper.getArgumentAnnotation(field);
            if (isLastArgument(declaredArgumentList, i)
                    && ArgumentParserMatcher.argumentParserDoesNotMatchFieldType(field, argumentAnnotation)) {
                List<Object> value = argumentList.stream()
                        .map(a -> parseValue(field, a, argumentAnnotation.parser(), argumentAnnotation.values()))
                        .collect(Collectors.toList());
                argumentList.clear();
                setFieldValue(command, field, value);
            } else {
                String value = argumentList.remove(0);
                Object parsedValue = parseValue(field, value, argumentAnnotation.parser(), argumentAnnotation.values());
                setFieldValue(command, field, parsedValue);
            }
        }
        if (!argumentList.isEmpty()) {
            throw new ValueParseException("Received unhandled arguments: " + argumentList);
        }
    }

    private static boolean isLastArgument(List<Field> declaredArgumentList, int i) {
        return i == declaredArgumentList.size() - 1;
    }

    private static void handleOptions(Object command, Map<String, String> optionMap) {
        Class<?> commandClass = command.getClass();
        FieldsProvider.getOptionStream(commandClass)
                .forEach((field) -> {
                    Cli.Option optionAnnotation = AnnotationHelper.getOptionAnnotation(field);
                    Optional<String> value = getOptionValue(optionMap, field);
                    if (value.isPresent()) {
                        Object parsedValue = parseValue(field, value.get(), optionAnnotation.parser(), optionAnnotation.values());
                        setFieldValue(command, field, parsedValue);
                    }
                });
    }

    private static Object parseValue(Field field, String value, Class<? extends ValueParser> valueParserClass, String[] values) {
        ValueParser valueParser = ValueParserProvider.getValueParser(field, valueParserClass);
        return AnnotationHelper.checkedValue(value, values)
                .map((Function<String, Object>) valueParser::parse)
                .orElseThrow(() -> new ValueParseException("value '" + value + "' not allowed, allowed are '" + Arrays.asList(values) + "'"));
    }
}

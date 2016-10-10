package com.github.peterpaul.cli;

import com.github.peterpaul.cli.parser.ValueParser;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProgramRunner {
    private final ArgumentParserMatcher argumentParserMatcher;
    private final HelpGenerator helpGenerator;
    private final ValueParserProvider valueParserProvider;

    public ProgramRunner() {
        valueParserProvider = new ValueParserProvider();
        argumentParserMatcher = new ArgumentParserMatcher(valueParserProvider);
        helpGenerator = new HelpGenerator(argumentParserMatcher);
    }

    private static boolean isLastArgument(List<Field> declaredArgumentList, int i) {
        return i == declaredArgumentList.size() - 1;
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
        Cli.Option optionAnnotation = field.getAnnotation(Cli.Option.class);
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

    public void run(String[] arguments, Object command) {
        try {
            parseOptions(arguments, command);
            parseArguments(arguments, command);
            CommandRunner.runCommand(command);
        } catch (ValueParseException e) {
            System.err.println(helpGenerator.generateHelp(command, e.getMessage()));
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
        List<Field> declaredArgumentList = FieldsProvider.getArgumentList(commandClass);
        for (int i = 0; i < declaredArgumentList.size(); i++) {
            Field field = declaredArgumentList.get(i);
            Cli.Argument argumentAnnotation = field.getAnnotation(Cli.Argument.class);
            if (isLastArgument(declaredArgumentList, i)
                    && argumentParserMatcher.argumentParserDoesNotMatchFieldType(field, argumentAnnotation)) {
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

    private void handleOptions(Object command, Map<String, String> optionMap) {
        Class<?> commandClass = command.getClass();
        FieldsProvider.getOptionStream(commandClass)
                .forEach((field) -> {
                    Cli.Option optionAnnotation = field.getAnnotation(Cli.Option.class);
                    Optional<String> value = getOptionValue(optionMap, field);
                    if (value.isPresent()) {
                        Object parsedValue = parseValue(field, value.get(), optionAnnotation.parser(), optionAnnotation.values());
                        setFieldValue(command, field, parsedValue);
                    }
                });
    }

    private Object parseValue(Field field, String value, Class<? extends ValueParser> valueParserClass, String[] values) {
        ValueParser valueParser = valueParserProvider.getValueParser(field, valueParserClass);
        return AnnotationHelper.checkedValue(value, values)
                .map(v -> valueParser.parse(v))
                .orElseThrow(() -> new ValueParseException("value '" + value + "' not allowed, allowed are '" + Arrays.asList(values) + "'"));
    }
}

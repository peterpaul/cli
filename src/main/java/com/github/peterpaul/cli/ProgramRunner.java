package com.github.peterpaul.cli;

import com.github.peterpaul.cli.exceptions.ValueParseException;
import com.github.peterpaul.cli.instantiator.InstantiatorSupplier;
import com.github.peterpaul.cli.parser.ValueParser;
import com.github.peterpaul.fn.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.github.peterpaul.fn.Function.mapper;
import static com.github.peterpaul.fn.Stream.stream;

public class ProgramRunner {
    public static final Function<Class, String> GET_COMMAND_NAME = new Function<Class, String>() {
        @Override
        public String apply(Class aClass) {
            return getCommandName(aClass);
        }
    };
    public static final Function<Class, Pair<String, Class>> GET_NAME_TO_CLASS_MAP = new Function<Class, Pair<String, Class>>() {
        @Override
        public Pair<String, Class> apply(Class aClass) {
            return Pair.pair(getCommandName(aClass), aClass);
        }
    };

    public static String getCommandName(Class aClass) {
        return AnnotationHelper.getCommandAnnotation(aClass).name();
    }

    public static void run(Object command, String[] arguments) {
        run(command, getArgumentList(arguments), getOptionMap(arguments));
    }

    public static Boolean run(Object command, List<String> argumentList, Map<String, String> optionMap) {
        try {
            Cli.Command commandAnnotation = AnnotationHelper.getCommandAnnotation(command);
            if (commandAnnotation.subCommands().length == 0) {
                runCommand(command, argumentList, optionMap);
            } else {
                runCompositeCommand(command, argumentList, optionMap);
            }
            return true;
        } catch (ValueParseException e) {
            System.err.println(HelpGenerator.generateHelp(command, e.getMessage()));
            return false;
        }
    }

    private static void runCommand(Object command, List<String> argumentList, Map<String, String> optionMap) {
        handleOptions(command, optionMap);
        handleArguments(command, argumentList);
        CommandRunner.runCommand(command);
    }

    private static void runCompositeCommand(final Object command, final List<String> argumentList, final Map<String, String> optionMap) {
        handleOptions(command, optionMap);
        final Cli.Command commandAnnotation = AnnotationHelper.getCommandAnnotation(command);
        final Function<String, Option<Class>> subCommandMapper = getSubCommandMapper(commandAnnotation);
        final String subCommandArgument = argumentList.remove(0);
        subCommandMapper.apply(subCommandArgument)
                .map(InstantiatorSupplier.instantiate())
                .map(new Function<Object, Boolean>() {
                    @Override
                    public Boolean apply(Object o) {
                        return run(o, argumentList, optionMap);
                    }
                })
                .or(new Supplier<Boolean>() {
                    @Override
                    public Boolean get() {
                        if (subCommandArgument.equals("help")) {
                            Object helpCommand = stream(argumentList)
                                    .first()
                                    .flatMap(new Function<String, Option<Object>>() {
                                        @Override
                                        public Option<Object> apply(String arg) {
                                            return subCommandMapper.apply(arg).map(InstantiatorSupplier.instantiate());
                                        }
                                    })
                                    .or(Supplier.of(command));
                            System.out.println(HelpGenerator.generateHelp(helpCommand));
                        } else {
                            String subCommandsString = stream(commandAnnotation.subCommands())
                                    .map(GET_COMMAND_NAME)
                                    .reduce(new Reduction<String>() {
                                        @Override
                                        public String apply(String s, String t) {
                                            return s + ", " + t;
                                        }
                                    })
                                    .or("");
                            throw new ValueParseException("Not a subcommand: '" + subCommandArgument + "', allowed are [" + subCommandsString + ']');
                        }
                        return true;
                    }
                });
    }

    private static Function<String, Option<Class>> getSubCommandMapper(Cli.Command commandAnnotation) {
        Map<String, Class> subCommandMap = stream(commandAnnotation.subCommands())
                .toMap(GET_NAME_TO_CLASS_MAP);
        return mapper(subCommandMap);
    }

    private static void setFieldValue(Object command, Field field, Object parsedValue) {
        try {
            field.setAccessible(true);
            field.set(command, parsedValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Option<String> getOptionValue(Map<String, String> optionMap, Field field) {
        Cli.Option optionAnnotation = AnnotationHelper.getOptionAnnotation(field);
        String name = FieldsProvider.getName(field, optionAnnotation.name());
        Option<String> valueFromCommandLine = stream(
                "--" + name,
                "-" + optionAnnotation.shortName())
                .map(mapper(optionMap))
                .filterMap(Function.<Option<String>>identity())
                .first();
        if (valueFromCommandLine.isPresent()) {
            return valueFromCommandLine;
        } else {
            return AnnotationHelper.fromEmpty(optionAnnotation.defaultValue());
        }
    }

    private static List<String> getArgumentList(String[] arguments) {
        return stream(arguments)
                .filter(new Predicate<String>() {
                    @Override
                    public Boolean apply(String s) {
                        return !s.startsWith("-");
                    }
                })
                .to(new ArrayList<String>());
    }

    private static Map<String, String> getOptionMap(String[] arguments) {
        return stream(arguments)
                .filter(new Predicate<String>() {
                    @Override
                    public Boolean apply(String s) {
                        return s.startsWith("-");
                    }
                })
                .toMap(ActualOptionParser.optionKey(), ActualOptionParser.optionValue());
    }

    private static void handleArguments(Object command, List<String> argumentList) {
        Class<?> commandClass = command.getClass();
        List<Field> declaredArgumentList = FieldsProvider.getArgumentList(commandClass);
        for (int i = 0; i < declaredArgumentList.size(); i++) {
            final Field field = declaredArgumentList.get(i);
            final Cli.Argument argumentAnnotation = AnnotationHelper.getArgumentAnnotation(field);
            if (isLastArgument(declaredArgumentList, i)
                    && ArgumentParserMatcher.argumentParserDoesNotMatchFieldType(field, argumentAnnotation)) {
                List<Object> value = stream(argumentList)
                        .map(new Function<String, Object>() {
                            @Override
                            public Object apply(String a) {
                                return parseValue(field, a, argumentAnnotation.parser(), argumentAnnotation.values());
                            }
                        })
                        .to(new ArrayList<>());
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

    private static void handleOptions(final Object command, final Map<String, String> optionMap) {
        Class<?> commandClass = command.getClass();
        FieldsProvider.getOptionStream(commandClass)
                .forEach(new Consumer<Field>() {
                    @Override
                    public void consume(Field field) {
                        Cli.Option optionAnnotation = AnnotationHelper.getOptionAnnotation(field);
                        Option<String> value = getOptionValue(optionMap, field);
                        if (value.isPresent()) {
                            Object parsedValue = parseValue(field, value.get(), optionAnnotation.parser(), optionAnnotation.values());
                            setFieldValue(command, field, parsedValue);
                        }
                    }
                });
    }

    private static Object parseValue(Field field, final String value, Class<? extends ValueParser> valueParserClass, final String[] values) {
        final ValueParser valueParser = ValueParserProvider.getValueParser(field, valueParserClass);
        return AnnotationHelper.checkedValue(value, values)
                .map(new Function<String, Object>() {
                    @Override
                    public Object apply(String s) {
                        return valueParser.parse(s);
                    }
                })
                .orThrow(new Supplier<RuntimeException>() {
                    @Override
                    public RuntimeException get() {
                        return new ValueParseException("value '" + value + "' not allowed, allowed are '" + Arrays.asList(values) + "'");
                    }
                });
    }
}

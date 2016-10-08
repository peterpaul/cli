package tk.kleinhaneveld.cli;

import tk.kleinhaneveld.cli.parser.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProgramRunner {
    private Map<Class, ValueParser> valueParserMap;

    public ProgramRunner() {
        valueParserMap = Stream.of(new BooleanValueParser(),
                new FileValueParser(),
                new IntValueParser(),
                new StringValueParser(),
                new UrlValueParser())
                .collect(Collectors.toMap(p -> p.getSupportedClass(), p -> p));
    }

    public void run(String[] arguments, Object command) {
        Class<?> commandClass = command.getClass();
        Cli.Command commandAnnotation = (Cli.Command) commandClass.getAnnotation(Cli.Command.class);
        Map<String, String> optionMap = Arrays.stream(arguments)
                .filter(s -> s.startsWith("-"))
                .collect(Collectors.toMap((option) -> optionKey(option), (option) -> optionValue(option)));
        List<String> argumentList = Arrays.stream(arguments)
                .filter(s -> !s.startsWith("-"))
                .collect(Collectors.toList());
        System.out.println("--- options");
        Arrays.stream(commandClass.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Cli.Option.class))
                .peek(System.out::println)
                .forEach((field) -> {
                    Cli.Option optionAnnotation = field.getAnnotation(Cli.Option.class);
                    Optional<String> value = getOptionValue(optionMap, optionAnnotation);
                    if (value.isPresent()) {
                        Object parsedValue = parseFieldValue(field, value);
                        setFieldValue(command, field, parsedValue);
                    }
                });
        System.out.println("--- arguments");
        Arrays.stream(commandClass.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Cli.Argument.class))
                .peek(System.out::println)
                .forEach((field) -> {
                    Cli.Argument argumentAnnotation = field.getAnnotation(Cli.Argument.class);
                    String value = argumentList.remove(0);
                    Object parsedValue = parseFieldValue(field, Optional.of(value));
                    if (parsedValue != null) {
                        setFieldValue(command, field, parsedValue);
                    }
                });
        Optional<Method> runMethod = Arrays.stream(commandClass.getDeclaredMethods())
                .peek(System.out::println)
                .filter(m -> m.getReturnType().isAnnotationPresent(Cli.Run.class))
                .peek(System.out::println)
                .findAny();
        System.out.println("" + runMethod);

        try {
            Method method = commandClass.getMethod("run", new Class[0]);
            runMethod = Optional.of(method);
        } catch (NoSuchMethodException e) {
            runMethod = Optional.empty();
        }
        runMethod.ifPresent(m -> {
            try {
                m.invoke(command);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setFieldValue(Object command, Field field, Object parsedValue) {
        try {
            field.setAccessible(true);
            field.set(command, parsedValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object parseFieldValue(Field field, Optional<String> value) {
        Object parsedValue = null;
        System.out.println("--type--" + field.getType());
        ValueParser valueParser = valueParserMap.get(field.getType());
        if (valueParser != null) {
            parsedValue = valueParser.parse(value.get());
        }
        return parsedValue;
    }

    private Optional<String> getOptionValue(Map<String, String> optionMap, Cli.Option optionAnnotation) {
        return Stream.of(optionAnnotation.name(), optionAnnotation.shortName())
                                    .filter(n -> n != null)
                                    .map(n -> optionMap.get(n))
                                    .filter(n -> n != null)
                                    .findAny();
    }

    public static String optionKey(String option) {
        int offset = option.indexOf("=");
        if (offset < 0) {
            return option;
        } else {
            return option.substring(0, offset);
        }
    }

    public static String optionValue(String option) {
        int offset = option.indexOf("=");
        if (offset < 0) {
            return "";
        } else {
            return option.substring(offset + 1, option.length());
        }
    }
}

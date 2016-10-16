package com.github.peterpaul.cli;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class HelpGenerator {
    private static final ImmutableSectionConfiguration TOP_LEVEL_SECTION = ImmutableSectionConfiguration.builder()
            .firstLineIndentation(0).indentation(4).lineWidth(80).build();
    private static final ImmutableSectionConfiguration ARGUMENT_LEVEL_SECTION = ImmutableSectionConfiguration.builder()
            .firstLineIndentation(4).indentation(8).lineWidth(80).build();
    private static final ImmutableSectionConfiguration OPTION_LEVEL_SECTION = ImmutableSectionConfiguration.builder()
            .firstLineIndentation(16).indentation(16).lineWidth(80).build();

    public static String generateHelp(Object command, String error) {
        return OutputHelper.format("Error: " + error, TOP_LEVEL_SECTION) + "\n\n" +
                generateHelp(command);
    }

    public static String generateHelp(Object command) {
        Cli.Command commandAnnotation = AnnotationHelper.getCommandAnnotation(command);
        if (commandAnnotation.subCommands().length == 0) {
            return getNameAndDescription(commandAnnotation) + "\n\n"
                    + getUsage(command) + "\n"
                    + getArgumentHelp(command) + "\n"
                    + getOptionHelp(command)
                    ;
        } else {
            return getNameAndDescription(commandAnnotation) + "\n\n"
                    + OutputHelper.format("USAGE: " + commandAnnotation.name() + " [OPTION...] COMMAND", TOP_LEVEL_SECTION) + "\n"
                    + getSubCommands(commandAnnotation) + "\n"
                    + getOptionHelp(commandAnnotation)
                    ;
        }
    }

    private static String getSubCommands(Cli.Command commandAnnotation) {
        return Arrays.stream(commandAnnotation.subCommands())
                .map(AnnotationHelper::getCommandAnnotation)
                .map(c -> OutputHelper.format(OutputHelper.ofSize(c.name(), 12) + c.description(), ARGUMENT_LEVEL_SECTION))
                .reduce("COMMAND:", (s, t) -> s + "\n" + t);
    }

    private static String getUsage(Object command) {
        Cli.Command commandAnnotation = AnnotationHelper.getCommandAnnotation(command);
        return OutputHelper.format("USAGE: " + commandAnnotation.name() + " [OPTION...] " +
                        FieldsProvider.getArgumentStream(command.getClass())
                                .map((arg) -> {
                                    Cli.Argument argumentAnnotation = AnnotationHelper.getArgumentAnnotation(arg);
                                    String nameString = FieldsProvider.getName(arg, argumentAnnotation.name());
                                    return ArgumentParserMatcher.argumentParserDoesNotMatchFieldType(arg, argumentAnnotation)
                                            ? "[" + nameString + "...]"
                                            : nameString;
                                })
                                .reduce("", (state, argumentName) -> state + " " + argumentName),
                TOP_LEVEL_SECTION);
    }

    private static String getArgumentHelp(Object command) {
        return FieldsProvider.getArgumentStream(command.getClass())
                .map(argumentField -> {
                    Cli.Argument argumentFieldAnnotation = AnnotationHelper.getArgumentAnnotation(argumentField);
                    return OutputHelper.ofSize(FieldsProvider.getName(argumentField, argumentFieldAnnotation.name()) + ":",
                            12) + argumentFieldAnnotation.description();
                })
                .map(argument -> OutputHelper.format(argument, ARGUMENT_LEVEL_SECTION))
                .reduce("WHERE:", (state, arg) -> (state + "\n" + arg));
    }

    private static String getOptionHelp(Object command) {
        return FieldsProvider.getOptionStream(command.getClass())
                .map(optionField -> {
                    Cli.Option annotation = AnnotationHelper.getOptionAnnotation(optionField);
                    String shortOptionString = Objects.equals(annotation.shortName(), '\0')
                            ? ""
                            : "-" + annotation.shortName() + ",";
                    String optionNameString = "--" + FieldsProvider.getName(optionField, annotation.name());
                    String defaultValueString = AnnotationHelper.fromEmpty(annotation.defaultValue()).map(defaultValue -> "default: '" + defaultValue + "'").orElse("");
                    String valuesString = getValueString(optionField);
                    String optionString = OutputHelper.ofSize(shortOptionString + optionNameString + "=" + optionField.getType().getSimpleName(), 12);
                    return OutputHelper.format(optionString + valuesString + defaultValueString,
                            ARGUMENT_LEVEL_SECTION) + '\n' +
                            OutputHelper.format(annotation.description(), OPTION_LEVEL_SECTION);
                })
                .reduce("OPTION:", (state, opt) -> (state + "\n" + opt));
    }

    private static String getValueString(Field optionField) {
        Cli.Option annotation = AnnotationHelper.getOptionAnnotation(optionField);
        Optional<Stream<String>> stringStream;
        if (isBoolean(optionField) && annotation.values().length == 0) {
            stringStream = Optional.of(Arrays.stream(new String[]{"true", "false"}));
        } else {
            stringStream = AnnotationHelper.valueStream(annotation.values());
        }
        return stringStream.map(v -> "('" + v.reduce((s, t) -> s + "', '" + t).get() + "') ").orElse("");
    }

    private static boolean isBoolean(Field optionField) {
        return optionField.getType() == Boolean.class || optionField.getType() == boolean.class;
    }

    private static String getNameAndDescription(Cli.Command commandAnnotation) {
        return OutputHelper.format(commandAnnotation.name() + " - " + commandAnnotation.description(), TOP_LEVEL_SECTION);
    }
}

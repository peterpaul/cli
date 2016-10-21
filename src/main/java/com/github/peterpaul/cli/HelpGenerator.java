package com.github.peterpaul.cli;

import com.github.peterpaul.cli.locale.Bundle;

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
        Bundle bundle = AnnotationHelper.getResourceBundle(commandAnnotation);
        if (commandAnnotation.subCommands().length == 0) {
            return getNameAndDescription(commandAnnotation, bundle) + "\n\n"
                    + getUsage(command) + "\n"
                    + getArgumentHelp(command, bundle) + "\n"
                    + getOptionHelp(command, bundle)
                    ;
        } else {
            return getNameAndDescription(commandAnnotation, bundle) + "\n\n"
                    + OutputHelper.format("USAGE: " + commandAnnotation.name() + " [OPTION...] COMMAND", TOP_LEVEL_SECTION) + "\n"
                    + getSubCommands(commandAnnotation, bundle) + "\n"
                    + getOptionHelp(commandAnnotation, bundle)
                    ;
        }
    }

    private static String getSubCommands(Cli.Command commandAnnotation, Bundle bundle) {
        return Arrays.stream(commandAnnotation.subCommands())
                .map(AnnotationHelper::getCommandAnnotation)
                .map(c -> OutputHelper.format(OutputHelper.ofSize(c.name(), 12) + bundle.apply(c.description()), ARGUMENT_LEVEL_SECTION))
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

    private static String getArgumentHelp(Object command, Bundle bundle) {
        return FieldsProvider.getArgumentStream(command.getClass())
                .map(arg -> {
                    Cli.Argument argumentAnnotation = AnnotationHelper.getArgumentAnnotation(arg);
                    return OutputHelper.ofSize(FieldsProvider.getName(arg, argumentAnnotation.name()) + ":",
                            12) + bundle.apply(argumentAnnotation.description());
                })
                .map(argument -> OutputHelper.format(argument, ARGUMENT_LEVEL_SECTION))
                .reduce("WHERE:", (state, arg) -> (state + "\n" + arg));
    }

    private static String getOptionHelp(Object command, Bundle bundle) {
        return FieldsProvider.getOptionStream(command.getClass())
                .map(option -> {
                    Cli.Option optionAnnotation = AnnotationHelper.getOptionAnnotation(option);
                    String shortOptionString = Objects.equals(optionAnnotation.shortName(), '\0')
                            ? ""
                            : "-" + optionAnnotation.shortName() + ",";
                    String optionNameString = "--" + FieldsProvider.getName(option, optionAnnotation.name());
                    String defaultValueString = AnnotationHelper.fromEmpty(optionAnnotation.defaultValue()).map(defaultValue -> "default: '" + defaultValue + "'").orElse("");
                    String valuesString = getValueString(option);
                    String optionString = OutputHelper.ofSize(shortOptionString + optionNameString + "=" + option.getType().getSimpleName(), 12);
                    return OutputHelper.format(optionString + valuesString + defaultValueString,
                            ARGUMENT_LEVEL_SECTION) + '\n' +
                            OutputHelper.format(bundle.apply(optionAnnotation.description()), OPTION_LEVEL_SECTION);
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

    private static String getNameAndDescription(Cli.Command commandAnnotation, Bundle bundle) {
        return OutputHelper.format(commandAnnotation.name() + " - " + bundle.apply(commandAnnotation.description()), TOP_LEVEL_SECTION);
    }
}

package com.github.peterpaul.cli;

import com.github.peterpaul.cli.locale.Bundle;
import com.github.peterpaul.fn.Function;
import com.github.peterpaul.fn.Option;
import com.github.peterpaul.fn.Stream;

import java.lang.reflect.Field;
import java.util.Objects;

import static com.github.peterpaul.fn.Reductions.join;

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

    private static String getSubCommands(Cli.Command commandAnnotation, final Bundle bundle) {
        return Stream.stream(commandAnnotation.subCommands())
                .map(new Function<Class, Cli.Command>() {
                    @Override
                    public Cli.Command apply(Class aClass) {
                        return AnnotationHelper.getCommandAnnotation(aClass);
                    }
                })
                .map(new Function<Cli.Command, String>() {
                    @Override
                    public String apply(Cli.Command c) {
                        return OutputHelper.format(OutputHelper.ofSize(c.name(), 12) + bundle.apply(c.description()), ARGUMENT_LEVEL_SECTION);
                    }
                })
                .reduce("COMMAND:", join("\n"));
    }

    private static String getUsage(Object command) {
        Cli.Command commandAnnotation = AnnotationHelper.getCommandAnnotation(command);
        return OutputHelper.format("USAGE: " + commandAnnotation.name() + " [OPTION...] " +
                        FieldsProvider.getArgumentStream(command.getClass())
                                .map(new Function<Field, String>() {
                                    @Override
                                    public String apply(Field arg) {
                                        Cli.Argument argumentAnnotation = AnnotationHelper.getArgumentAnnotation(arg);
                                        String nameString = FieldsProvider.getName(arg, argumentAnnotation.name());
                                        return ArgumentParserMatcher.argumentParserDoesNotMatchFieldType(arg, argumentAnnotation)
                                                ? "[" + nameString + "...]"
                                                : nameString;
                                    }
                                })
                                .reduce("", join(" ")),
                TOP_LEVEL_SECTION);
    }

    private static String getArgumentHelp(Object command, final Bundle bundle) {
        return FieldsProvider.getArgumentStream(command.getClass())
                .map(new Function<Field, String>() {
                    @Override
                    public String apply(Field arg) {
                        Cli.Argument argumentAnnotation = AnnotationHelper.getArgumentAnnotation(arg);
                        return OutputHelper.ofSize(FieldsProvider.getName(arg, argumentAnnotation.name()) + ":",
                                12) + bundle.apply(argumentAnnotation.description());
                    }
                })
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) {
                        return OutputHelper.format(s, ARGUMENT_LEVEL_SECTION);
                    }
                })
                .reduce("WHERE:", join("\n"));
    }

    private static String getOptionHelp(Object command, final Bundle bundle) {
        return FieldsProvider.getOptionStream(command.getClass())
                .map(new Function<Field, String>() {
                    @Override
                    public String apply(Field option) {
                        Cli.Option optionAnnotation = AnnotationHelper.getOptionAnnotation(option);
                        String shortOptionString = Objects.equals(optionAnnotation.shortName(), '\0')
                                ? ""
                                : "-" + optionAnnotation.shortName() + ",";
                        String optionNameString = "--" + FieldsProvider.getName(option, optionAnnotation.name());
                        String defaultValueString = AnnotationHelper
                                .fromEmpty(optionAnnotation.defaultValue())
                                .map(new Function<String, String>() {
                                    @Override
                                    public String apply(String defaultValue) {
                                        return "default: '" + defaultValue + "'";
                                    }
                                })
                                .or("");
                        String valuesString = getValueString(option);
                        String optionString = OutputHelper.ofSize(shortOptionString + optionNameString + "=" + option.getType().getSimpleName(), 12);
                        return OutputHelper.format(optionString + valuesString + defaultValueString,
                                ARGUMENT_LEVEL_SECTION) + '\n' +
                                OutputHelper.format(bundle.apply(optionAnnotation.description()), OPTION_LEVEL_SECTION);
                    }
                })
                .reduce("OPTION:", join("\n"));
    }

    private static String getValueString(Field optionField) {
        Cli.Option annotation = AnnotationHelper.getOptionAnnotation(optionField);
        Option<Stream<String>> stringStream;
        if (isBoolean(optionField) && annotation.values().length == 0) {
            stringStream = Option.of(Stream.stream("true", "false"));
        } else {
            stringStream = AnnotationHelper.valueStream(annotation.values());
        }
        return stringStream.map(new Function<Stream<String>, String>() {
            @Override
            public String apply(Stream<String> v) {
                return "('" + v.reduce(join("', '")).get() + "') ";
            }
        }).or("");
    }

    private static boolean isBoolean(Field optionField) {
        return optionField.getType() == Boolean.class || optionField.getType() == boolean.class;
    }

    private static String getNameAndDescription(Cli.Command commandAnnotation, Bundle bundle) {
        return OutputHelper.format(commandAnnotation.name() + " - " + bundle.apply(commandAnnotation.description()), TOP_LEVEL_SECTION);
    }
}

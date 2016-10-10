package com.github.peterpaul.cli;

import java.util.Objects;

public class HelpGenerator {
    private static final ImmutableSectionConfiguration TOP_LEVEL_SECTION = ImmutableSectionConfiguration.builder()
            .firstLineIndentation(0).indentation(4).lineWidth(80).build();
    private static final ImmutableSectionConfiguration ARGUMENT_LEVEL_SECTION = ImmutableSectionConfiguration.builder()
            .firstLineIndentation(4).indentation(8).lineWidth(80).build();
    private static final ImmutableSectionConfiguration OPTION_LEVEL_SECTION = ImmutableSectionConfiguration.builder()
            .firstLineIndentation(16).indentation(16).lineWidth(80).build();
    private final ArgumentParserMatcher argumentParserMatcher;

    public HelpGenerator(ArgumentParserMatcher argumentParserMatcher) {
        this.argumentParserMatcher = argumentParserMatcher;
    }

    public String generateHelp(Object command, String error) {
        return OutputHelper.format("Error: " + error, TOP_LEVEL_SECTION) + "\n\n" +
                generateHelp(command);
    }

    public String generateHelp(Object command) {
        Cli.Command commandAnnotation = command.getClass().getAnnotation(Cli.Command.class);

        return getNameAndDescription(commandAnnotation) + "\n"
                + getUsage(command) + "\n"
                + getArgumentHelp(command) + "\n"
                + getOptionHelp(command)
                ;
    }

    private String getUsage(Object command) {
        Cli.Command commandAnnotation = command.getClass().getAnnotation(Cli.Command.class);
        return OutputHelper.format("USAGE: " + commandAnnotation.name() + " [OPTION...] " +
                        FieldsProvider.getArgumentStream(command.getClass())
                                .map((arg) -> {
                                    Cli.Argument argumentAnnotation = arg.getAnnotation(Cli.Argument.class);
                                    String nameString = FieldsProvider.getName(arg, argumentAnnotation.name());
                                    return argumentParserMatcher.argumentParserDoesNotMatchFieldType(arg, argumentAnnotation)
                                            ? "[" + nameString + "...]"
                                            : nameString;
                                })
                                .reduce("", (state, argumentName) -> state + " " + argumentName),
                TOP_LEVEL_SECTION);
    }

    private String getArgumentHelp(Object command) {
        return FieldsProvider.getArgumentStream(command.getClass())
                .map(argumentField -> {
                    Cli.Argument argumentFieldAnnotation = argumentField.getAnnotation(Cli.Argument.class);
                    return OutputHelper.ofSize(FieldsProvider.getName(argumentField, argumentFieldAnnotation.name()) + ":",
                            12) + argumentFieldAnnotation.description();
                })
                .map(argument -> OutputHelper.format(argument, ARGUMENT_LEVEL_SECTION))
                .reduce("WHERE:", (state, arg) -> (state + "\n" + arg));
    }

    private String getOptionHelp(Object command) {
        return FieldsProvider.getOptionStream(command.getClass())
                .map(optionField -> {
                    Cli.Option annotation = optionField.getAnnotation(Cli.Option.class);
                    String shortOptionString = Objects.equals(annotation.shortName(),
                            '\0')
                            ? ""
                            : "-" + annotation.shortName() + ",";
                    String optionNameString = "--" + FieldsProvider.getName(optionField, annotation.name());
                    String defaultValueString = AnnotationHelper.fromEmpty(annotation.defaultValue()).map(defaultValue -> "default: " + defaultValue).orElse("");
                    String valuesString = AnnotationHelper.valueStream(annotation.values()).map(v -> "(" + v.reduce((s, t) -> s + "|" + t).get() + ") ").orElse("");
                    return OutputHelper.format(OutputHelper.ofSize(shortOptionString + optionNameString, 12) + valuesString + defaultValueString,
                            ARGUMENT_LEVEL_SECTION) + '\n' +
                            OutputHelper.format(annotation.description(), OPTION_LEVEL_SECTION);
                })
                .reduce("OPTIONS:", (state, opt) -> (state + "\n" + opt));
    }

    private static String getNameAndDescription(Cli.Command commandAnnotation) {
        return OutputHelper.format(commandAnnotation.name() + " - " + commandAnnotation.description(), TOP_LEVEL_SECTION);
    }
}

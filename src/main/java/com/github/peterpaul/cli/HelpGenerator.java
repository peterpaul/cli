package com.github.peterpaul.cli;

public class HelpGenerator {
    private static final ImmutableSectionConfiguration TOP_LEVEL_SECTION = ImmutableSectionConfiguration.builder()
            .firstLineIndentation(0).indentation(4).lineWidth(80).build();

    public static String generateHelp(Object command, String error) {
        return OutputHelper.format("Error: " + error, TOP_LEVEL_SECTION) + "\n\n" +
                generateHelp(command);
    }

    public static String generateHelp(Object command) {
        Cli.Command commandAnnotation = command.getClass().getAnnotation(Cli.Command.class);

        return getNameAndDescription(commandAnnotation) + "\n"
                + getUsage(command);
    }

    private static String getUsage(Object command) {
        Cli.Command commandAnnotation = command.getClass().getAnnotation(Cli.Command.class);
        return OutputHelper.format("USAGE: " + commandAnnotation.name() +
                        FieldsProvider.getArgumentStream(command.getClass())
                                .map((arg) -> FieldsProvider.getName(arg, arg.getAnnotation(Cli.Argument.class).name()))
                                .reduce("", (state, argumentName) -> state + " " + argumentName),
                TOP_LEVEL_SECTION);
    }

    private static String getNameAndDescription(Cli.Command commandAnnotation) {
        return OutputHelper.format(commandAnnotation.name() + " - " + commandAnnotation.description(), TOP_LEVEL_SECTION);
    }
}

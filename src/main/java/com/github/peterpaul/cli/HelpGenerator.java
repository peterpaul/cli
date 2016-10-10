package com.github.peterpaul.cli;

public class HelpGenerator {

    public static final ImmutableSectionConfiguration TOP_LEVEL_SECTION = ImmutableSectionConfiguration.builder()
            .firstLineIndentation(0).indentation(4).lineWidth(80).build();

    public static String generateHelp(Object command) {
        Cli.Command commandAnnotation = command.getClass().getAnnotation(Cli.Command.class);

        return OutputHelper.format(commandAnnotation.name() + " - " + commandAnnotation.description(), TOP_LEVEL_SECTION);
    }

    public static String generateHelp(Object command, String error) {
        return OutputHelper.format("Error: " + error, TOP_LEVEL_SECTION) + "\n\n" +
                generateHelp(command);
    }
}

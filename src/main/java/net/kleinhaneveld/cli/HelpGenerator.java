package net.kleinhaneveld.cli;

import net.kleinhaneveld.cli.locale.Bundle;
import net.kleinhaneveld.fn.Function;
import net.kleinhaneveld.fn.Option;
import net.kleinhaneveld.fn.Stream;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Objects;

import static net.kleinhaneveld.cli.AnnotationHelper.getCommandAnnotation;
import static net.kleinhaneveld.cli.AnnotationHelper.getCommandName;
import static net.kleinhaneveld.fn.Reductions.join;

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
        Cli.Command commandAnnotation = getCommandAnnotation(command);
        Bundle bundle = AnnotationHelper.getResourceBundle(commandAnnotation);
        if (commandAnnotation.subCommands().length == 0) {
            return getNameAndDescription(command, bundle) + "\n\n"
                    + getUsage(command) + "\n"
                    + getArgumentHelp(command, bundle)
                    + getOptionHelp(command, bundle)
                    ;
        } else {
            return getNameAndDescription(command, bundle) + "\n\n"
                    + OutputHelper.format("USAGE: " + getCommandName(command.getClass()) + getUsageOptions(command) + " COMMAND", TOP_LEVEL_SECTION) + "\n"
                    + getSubCommands(commandAnnotation, bundle) + "\n"
                    + getOptionHelp(commandAnnotation, bundle)
                    ;
        }
    }

    private static String getSubCommands(Cli.Command commandAnnotation, final Bundle bundle) {
        return Stream.stream(commandAnnotation.subCommands())
                .map(new Function<Class, String>() {
                    @Override
                    public String apply(Class aClass) {
                        String commandName = OutputHelper.format(getCommandName(aClass), ARGUMENT_LEVEL_SECTION);
                        String description = OutputHelper.format(bundle.apply(getCommandAnnotation(aClass).description()), OPTION_LEVEL_SECTION);
                        return commandName + "\n" + description;
                    }
                })
                .reduce("COMMAND:", join("\n"));
    }

    private static String getUsage(Object command) {
        return OutputHelper.format("USAGE: " + getCommandName(command.getClass()) + getUsageOptions(command) +
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

    private static String getUsageOptions(Object command) {
        return FieldsProvider
                .getOptionStream(command.getClass())
                .first()
                .map(new Function<Field, String>() {
                    @Nonnull
                    @Override
                    public String apply(@Nonnull Field field) {
                        return " [OPTION...]";
                    }
                })
                .or("");
    }

    private static String getArgumentHelp(Object command, final Bundle bundle) {
        return FieldsProvider.getArgumentStream(command.getClass())
                .map(new Function<Field, String>() {
                    @Override
                    public String apply(Field arg) {
                        Cli.Argument argumentAnnotation = AnnotationHelper.getArgumentAnnotation(arg);
                        return OutputHelper.format(FieldsProvider.getName(arg, argumentAnnotation.name()), ARGUMENT_LEVEL_SECTION) + "\n" +
                                OutputHelper.format(bundle.apply(argumentAnnotation.description()), OPTION_LEVEL_SECTION);
                    }
                })
                .reduce(join("\n"))
                .map(new Function<String, String>() {
                    @Nonnull
                    @Override
                    public String apply(@Nonnull String argumentHelp) {
                        return "WHERE:\n" + argumentHelp + "\n";
                    }
                })
                .or("");
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
                .reduce(join("\n"))
                .map(new Function<String, String>() {
                    @Nonnull
                    @Override
                    public String apply(@Nonnull String optionHelp) {
                        return "OPTION:\n" + optionHelp + "\n";
                    }
                })
                .or("");
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

    private static String getNameAndDescription(Object command, Bundle bundle) {
        String commandName = getCommandName(command.getClass());
        String description = getCommandAnnotation(command.getClass()).description();
        return OutputHelper.format(commandName + "\n" + bundle.apply(description), TOP_LEVEL_SECTION);
    }
}

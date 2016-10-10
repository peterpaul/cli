package com.github.peterpaul.cli;

/*
 * A composite command cannot have arguments, it can have options.
 */
@Cli.Command(
        name = "composite",
        description = "This command bundles other commands",
        subCommands = {TestCommand.class}
)
public class TestCompositeCommand {
}
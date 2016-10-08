package tk.kleinhaneveld.cli.test;

import tk.kleinhaneveld.cli.Cli;

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

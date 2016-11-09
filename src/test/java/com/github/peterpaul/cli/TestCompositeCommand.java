package com.github.peterpaul.cli;

import com.github.peterpaul.fn.Runner;

/*
 * A composite command cannot have arguments, it can have options.
 */
@Cli.Command(
        name = "composite",
        description = "This command bundles other commands",
        subCommands = {TestCommand.class}
)
public class TestCompositeCommand {
    @Cli.Run
    void run(Runner subCommand) {
        System.out.println("---before");
        try {
            subCommand.run();
        } finally {
            System.out.println("---after");
        }
    }
}

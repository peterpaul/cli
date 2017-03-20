package net.kleinhaneveld.cli.examples;

import net.kleinhaneveld.cli.Cli;
import net.kleinhaneveld.cli.parser.StringValueParser;

import java.util.List;

@Cli.Command(description = "Demonstration of variable argument list. This description is deliberately very long, to be able to test how long descriptions are being rendered by the HelpGenerator.")
public class ArgumentListCommand {
    @Cli.Argument(description = "List of strings.", parser = StringValueParser.class)
    private List<String> args;

    public void run() {
        System.out.println(args);
    }
}

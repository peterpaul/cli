package net.kleinhaneveld.cli.examples;

import net.kleinhaneveld.cli.Cli;
import net.kleinhaneveld.cli.ProgramRunner;

@Cli.Command(
        description = "Composite command example",
        subCommands = {HelloWorld.class, Greeter.class, GreeterMyType.class}
)
public class ExampleProgram {
    public static void main(String[] args) {
        ProgramRunner.run(ExampleProgram.class, args);
    }
}

package com.github.peterpaul.cli.examples;

import com.github.peterpaul.cli.Cli;
import com.github.peterpaul.cli.ProgramRunner;

@Cli.Command(
        description = "Composite command example",
        subCommands = {HelloWorld.class, Greeter.class, GreeterMyType.class}
)
public class ExampleProgram {
    public static void main(String[] args) {
        ProgramRunner.run(ExampleProgram.class, args);
    }
}

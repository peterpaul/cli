package net.kleinhaneveld.cli.examples;

import net.kleinhaneveld.cli.Cli;
import net.kleinhaneveld.cli.ProgramRunner;

@Cli.Command(description = "Minimal example")
public class HelloWorld {
    public static void main(String[] args) {
        ProgramRunner.run(HelloWorld.class, args);
    }

    public void run() {
        System.out.println("Hello World");
    }
}

package com.github.peterpaul.cli.examples;

import com.github.peterpaul.cli.Cli;
import com.github.peterpaul.cli.ProgramRunner;

@Cli.Command(description = "Minimal example")
public class HelloWorld {
    public static void main(String[] args) {
        ProgramRunner.run(HelloWorld.class, args);
    }

    public void run() {
        System.out.println("Hello World");
    }
}

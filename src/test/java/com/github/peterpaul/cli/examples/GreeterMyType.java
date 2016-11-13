package com.github.peterpaul.cli.examples;

import com.github.peterpaul.cli.Cli;
import com.github.peterpaul.cli.ProgramRunner;

@Cli.Command(name = "GreeterMyType", description = "some command")
public class GreeterMyType {
    @Cli.Option(description = "some option", shortName = 'U')
    private boolean uppercase;

    @Cli.Argument(description = "some argument")
    private MyType who;

    public static void main(String[] args) {
        ProgramRunner.run(GreeterMyType.class, args);
    }

    @Cli.Run
    public void perform() {
        String value = "Hello " + who.getValue();
        if (uppercase) {
            value = value.toUpperCase();
        }
        System.out.println(value);
    }
}

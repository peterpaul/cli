package net.kleinhaneveld.cli.examples;

import net.kleinhaneveld.cli.Cli;
import net.kleinhaneveld.cli.ProgramRunner;

@Cli.Command(name = "hello", description = "Example command using all cli annotations.")
public class Greeter {
    @Cli.Option(description = "some option", shortName = 'U')
    private boolean uppercase;

    @Cli.Argument(description = "some argument")
    private String who;

    public static void main(String[] args) {
        ProgramRunner.run(Greeter.class, args);
    }

    @Cli.Run
    public void perform() {
        String value = "Hello " + who;
        if (uppercase) {
            value = value.toUpperCase();
        }
        System.out.println(value);
    }
}

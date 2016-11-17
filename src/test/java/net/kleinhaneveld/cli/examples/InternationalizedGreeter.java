package net.kleinhaneveld.cli.examples;

import net.kleinhaneveld.cli.Cli;
import net.kleinhaneveld.cli.ProgramRunner;

import java.util.Locale;
import java.util.ResourceBundle;

@Cli.Command(name = "hello", description = "command.hello", resourceBundle = "greeter")
public class InternationalizedGreeter {
    @Cli.Option(description = "option.uppercase", shortName = 'U')
    private boolean uppercase;

    @Cli.Argument(description = "argument.who")
    private String who;

    public static void main(String[] args) {
        ProgramRunner.run(InternationalizedGreeter.class, args);
    }

    public void run() {
        ResourceBundle bundle = ResourceBundle.getBundle("greeter", Locale.getDefault());
        String value = bundle.getString("hello") + " " + who;
        if (uppercase) {
            value = value.toUpperCase();
        }
        System.out.println(value);
    }
}

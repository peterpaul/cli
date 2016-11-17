package net.kleinhaneveld.cli.examples;

import net.kleinhaneveld.cli.Cli;
import net.kleinhaneveld.cli.ProgramRunner;
import net.kleinhaneveld.fn.Runner;

@Cli.Command(
        description = "Transaction subcommands example",
        subCommands = {HelloWorld.class, Greeter.class, GreeterMyType.class}
)
public class TransactionalCommand {
    public static void main(String[] args) {
        ProgramRunner.run(TransactionalCommand.class, args);
    }

    void run(Runner subCommand) {
        Transaction transaction = Transaction.begin();
        try {
            subCommand.run();
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
        }
    }
}

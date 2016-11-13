package com.github.peterpaul.cli.examples;

import com.github.peterpaul.cli.Cli;
import com.github.peterpaul.cli.ProgramRunner;
import com.github.peterpaul.fn.Runner;

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

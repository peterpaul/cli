package net.kleinhaneveld.cli.examples;

public class Transaction {
    public static Transaction begin() {
        System.out.println("BEGIN TRAN");
        return new Transaction();
    }

    public void commit() {
        System.out.println("COMMIT");
    }

    public void rollback() {
        System.out.println("ROLLBACK");
    }
}

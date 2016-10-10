package tk.kleinhaneveld.cli;

public class ValueParseException extends RuntimeException {
    public ValueParseException(Throwable cause) {
        super(cause);
    }

    public ValueParseException(String msg) {
        super(msg);
    }
}

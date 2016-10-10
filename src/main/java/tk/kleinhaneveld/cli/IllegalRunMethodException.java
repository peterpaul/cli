package tk.kleinhaneveld.cli;

public class IllegalRunMethodException extends RuntimeException {
    public IllegalRunMethodException(String message) {
        super(message);
    }

    public IllegalRunMethodException(String message, Throwable cause) {
        super(message, cause);
    }
}

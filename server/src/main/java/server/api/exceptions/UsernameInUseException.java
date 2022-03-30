package server.api.exceptions;

public class UsernameInUseException extends IllegalStateException {
    /**
     * No-args constructor.
     */
    public UsernameInUseException() {
        super("Username already exists");
    }

    /**
     * Constructor with message.
     *
     * @param message the exception message
     */
    public UsernameInUseException(String message) {
        super(message);
    }
}

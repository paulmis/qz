package server.api.exceptions;

/**
 * Thrown when the user with the given credentials already exists.
 */
public class UserAlreadyExistsException extends IllegalStateException {
    /**
     * No-args constructor.
     */
    public UserAlreadyExistsException() {
        super("User already exists");
    }

    /**
     * Constructor with message.
     *
     * @param message the exception message
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

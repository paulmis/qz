package server.api.exceptions;

/**
 * Thrown when a user is not found, but required.
 */
public class UserNotFoundException extends IllegalStateException {
    /**
     * Default constructor.
     */
    public UserNotFoundException() {
        super("User doesn't exist");
    }

    /**
     * Constructor with message.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}

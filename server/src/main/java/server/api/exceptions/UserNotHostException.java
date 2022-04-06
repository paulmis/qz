package server.api.exceptions;

/**
 * Exception that is to be used if a non host player does
 * a host action.
 */
public class UserNotHostException extends IllegalStateException {
    /**
     * No-arg constructor.
     */
    public UserNotHostException() {
        super("The user tried to do a host action without being one.");
    }

    /**
     * Default constructor.
     *
     * @param message exception message
     */
    public UserNotHostException(String message) {
        super(message);
    }
}

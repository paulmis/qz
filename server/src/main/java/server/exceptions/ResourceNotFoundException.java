package server.exceptions;

/**
 * Exception thrown when a storage item is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * ResourceNotFoundException constructor.
     *
     * @param message The message to be displayed.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * ResourceNotFoundException constructor.
     *
     * @param message The message to be displayed.
     * @param cause The cause of the exception.
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

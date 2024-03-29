package server.exceptions;

/**
 * Exception thrown when there is a problem with the storage.
 */
public class StorageException extends RuntimeException {
    /**
     * StorageException constructor.
     *
     * @param message the exception message.
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * StorageException constructor.
     *
     * @param message the exception message.
     * @param cause the exception cause.
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

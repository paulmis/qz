package server.exceptions;

import lombok.Generated;

/**
 * Exception thrown when there is a problem with the storage.
 */
@Generated
public class StorageException extends RuntimeException {
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

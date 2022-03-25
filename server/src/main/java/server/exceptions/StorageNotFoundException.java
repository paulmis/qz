package server.exceptions;

import lombok.Generated;

/**
 * Exception thrown when a storage item is not found.
 */
@Generated
public class StorageNotFoundException extends RuntimeException {
    public StorageNotFoundException(String message) {
        super(message);
    }

    public StorageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

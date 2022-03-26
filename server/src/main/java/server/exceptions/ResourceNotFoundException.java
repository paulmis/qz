package server.exceptions;

import lombok.Generated;

/**
 * Exception thrown when a storage item is not found.
 */
@Generated
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

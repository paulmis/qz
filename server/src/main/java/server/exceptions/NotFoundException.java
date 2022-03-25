package server.exceptions;

import lombok.Generated;

/**
 * Exception thrown when a storage item is not found.
 */
@Generated
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

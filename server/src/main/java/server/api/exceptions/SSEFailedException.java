package server.api.exceptions;

import java.io.IOException;

/**
 * Thrown when the SSE send fails.
 */
public class SSEFailedException extends IOException {
    /**
     * No-arg constructor.
     */
    public SSEFailedException() {
        super("SSE send failed.");
    }

    /**
     * Default constructor.
     *
     * @param message exception messahe
     */
    public SSEFailedException(String message) {
        super(message);
    }
}

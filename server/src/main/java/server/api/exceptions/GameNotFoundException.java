package server.api.exceptions;

/**
 * Thrown when a game is not found, but is required.
 */
public class GameNotFoundException extends IllegalStateException {
    /**
     * No-args constructor.
     */
    public GameNotFoundException() {
        super("Game doesn't exist");
    }
}


package server.api.exceptions;

/**
 * Thrown when a lobby is not found, but is required.
 */
public class LobbyNotFoundException extends IllegalStateException {
    /**
     * No-args constructor.
     */
    public LobbyNotFoundException() {
        super("Lobby doesn't exist");
    }
}

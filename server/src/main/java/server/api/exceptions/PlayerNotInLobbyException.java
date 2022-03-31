package server.api.exceptions;

/**
 * Throw when a player is expected to be in a lobby, but isn't.
 */
public class PlayerNotInLobbyException extends IllegalStateException {
    /**
     * No-args constructor.
     */
    public PlayerNotInLobbyException() {
        super("Player is not in a lobby.");
    }
}

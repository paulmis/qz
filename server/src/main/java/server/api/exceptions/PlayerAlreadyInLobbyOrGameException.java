package server.api.exceptions;

/**
 * Thrown when a player is already in a lobby a game, but shouldn't be.
 */
public class PlayerAlreadyInLobbyOrGameException extends IllegalStateException {
    /**
     * No-args constructor.
     */
    public PlayerAlreadyInLobbyOrGameException() {
        super("Already in a lobby or game");
    }

    /**
     * Constructor with message.
     *
     * @param message the exception message
     */
    public PlayerAlreadyInLobbyOrGameException(String message) {
        super(message);
    }
}

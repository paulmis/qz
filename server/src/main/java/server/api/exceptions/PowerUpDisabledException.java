package server.api.exceptions;

/**
 * Exception that is to be used if the players can't play
 * power-ups at this current time.
 */
public class PowerUpDisabledException extends IllegalStateException {
    /**
     * No-arg constructor.
     */
    public PowerUpDisabledException() {
        super("Powers-ups are disabled right now.");
    }

    /**
     * Default constructor.
     *
     * @param message exception message
     */
    public PowerUpDisabledException(String message) {
        super(message);
    }
}

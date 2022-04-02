package server.api.exceptions;

/**
 * Thrown when a player tries to use an already used power-up.
 */
public class PowerUpAlreadyUsedException extends IllegalStateException {
    /**
     * No-args constructor.
     */
    public PowerUpAlreadyUsedException() {
        super("This power-up has already been used by you.");
    }
}

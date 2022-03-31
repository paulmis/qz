package server.api.exceptions;

import lombok.extern.slf4j.Slf4j;
import server.database.entities.User;
import server.database.entities.game.Game;

/**
 * Thrown when a user tries to join a lobby that has already started.
 */
@Slf4j
public class GameAlreadyStartedException extends IllegalStateException {
    /**
     * Default constructor.
     *
     * @param user the user who tried to join a game that has already started
     * @param game the game that has already started
     */
    public GameAlreadyStartedException(User user, Game<?> game) {
        super("The game has already started.");
        log.error("User {} tried to join game {} that has already started.", user.getId(), game.getId());
    }
}

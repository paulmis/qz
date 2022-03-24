package server.database.entities.utils;

import java.util.Optional;

/**
 * Interface of games that implement stages.
 */
public interface StagedGame {
    /**
     * Get the current stage.
     *
     * @return The current stage of the game.
     */
    Optional<GameStage> getStage();

    /**
     * Progress the game by one stage.
     */
    void nextStage() throws IllegalStateException;
}

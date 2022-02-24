package commons.entities.game;

/**
 * Enum stores the status of a Game object.
 */
public enum GameStatus {
    /**
     * Game has been created, but not started.
     */
    CREATED,
    /**
     * Game has been started and is in-progress.
     */
    ONGOING,
    /**
     * Game has been completed.
     */
    FINISHED
}

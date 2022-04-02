package commons.entities.messages;

/**
 * The enum of SSEEvent messages.
 * On the client these get mapped to event handlers.
 */
public enum SSEMessageType {
    /**
     * Confirms that the SSE has been properly initialized.
     */
    INIT,
    /**
     * Sent when a lobby is modified.
     */
    LOBBY_MODIFIED,
    /**
     * Sent when a lobby is deleted.
     */
    LOBBY_DELETED,
    /**
     * Sent when a lobby starts.
     */
    GAME_START,
    /**
     * Sent when a player leaves the game.
     */
    PLAYER_LEFT,
    /**
     * Sent when a player re-joins the game.
     */
    PLAYER_REJOINED,
    /**
     * Sent when the client should show a new question.
     */
    START_QUESTION,
    /**
     * Sent when the client should show answer to the current question.
     */
    STOP_QUESTION,
    /**
     * Sent when the client should show the in-game leaderboard.
     */
    SHOW_LEADERBOARD,
    /**
     * Sent when the game ends.
     */
    GAME_END,
    /**
     * Sent when a new message is sent in a lobby or game.
     */
    CHAT_MESSAGE,
    /**
     * Sent when a power-up is played
     */
    POWER_UP_PLAYED,
}

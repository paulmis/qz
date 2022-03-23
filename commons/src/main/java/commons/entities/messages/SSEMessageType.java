package commons.entities.messages;

/**
 * The enum of SSEEvent messages.
 * On the client these get mapped to event handlers.
 */
public enum SSEMessageType {
    INIT,
    PLAYER_LEFT,
    GAME_START,
    START_QUESTION,
    STOP_QUESTION
}

package commons.entities.messages;

/**
 * All supported SSE message types.
 */
public enum MessageType {
    /**
     * A message that is sent to the client when the client has successfully connected to the server.
     */
    INITIALIZATION,
    /**
     * A new question has been started.
     */
    QUESTION_START,
    /**
     * A question has been stopped - no more answers are allowed.
     */
    QUESTION_STOP,
}

package client.utils.communication;

/**
 * Indicates the class can retrieve SSE events through functions
 * annotated with @SSEEventHandler.
 */
public interface SSESource {
    /**
     * Resets the context of the handler to this class.
     * Since classes can override responses elicited by SSEEvents, an SSEHandler is
     * bound to a single controller instance at a time. When the controller context
     * changes (e.g. when entering a game), the SSEHandler must be re-bound.
     *
     * @param handler the handler to bind
     */
    void bindHandler(SSEHandler handler);
}

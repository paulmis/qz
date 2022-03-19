package server.utils;

import commons.entities.messages.MessageType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Utilities to help with the communication between the client and the server.
 */
public class CommunicationHelpers {
    /**
     * Create an SSE event from data.
     *
     * @param messageType The type of message to send.
     * @param message The message to send.
     * @return The SSE event builder.
     */
    public static SseEmitter.SseEventBuilder createSseEvent(MessageType messageType, Object message) {
        return SseEmitter.event().name(messageType.name()).data(message);
    }
}

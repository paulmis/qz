package server.utils;

import commons.entities.messages.MessageType;
import java.util.Set;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
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
     * @return The built SSE event.
     */
    public static Set<ResponseBodyEmitter.DataWithMediaType> createSseEvent(MessageType messageType, Object message) {
        return SseEmitter.event().name(messageType.name()).data(message).build();
    }
}

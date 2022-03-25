package server.utils;

import commons.entities.messages.SSEMessage;
import java.util.Set;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Utilities to help with the communication between the client and the server.
 */
public class SSE {
    /**
     * Create an SSE event from data.
     *
     * @param message The message to send.
     * @return The built SSE event.
     */
    public static SseEmitter.SseEventBuilder createEvent(SSEMessage message) {
        SseEmitter.SseEventBuilder builder = SseEmitter.event().name(message.getType().name());
        if (message.getData() != null) {
            builder.data(message.getData());
        }
        return builder;
    }
}

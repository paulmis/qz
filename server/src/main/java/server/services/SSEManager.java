package server.services;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Manager class to handle SSE emitters.
 */
@Service
@Slf4j
public class SSEManager {
    private Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Add a new SSE emitter.
     *
     * @param userId User ID to add SSE emitter for.
     * @return The SSE emitter.
     */
    public SseEmitter register(UUID userId, SseEmitter emitter) {
        if (emitters.containsKey(userId)) {
            emitters.get(userId).complete();
            emitters.remove(userId);
            log.debug("Removed existing SSE emitter for user {}", userId);
        }

        emitters.put(userId, emitter);
        return emitter;
    }

    /**
     * Remove an SSE emitter.
     *
     * @param userId User ID to remove SSE emitter for.
     * @return Whether the SSE emitter was successfully removed or not.
     */
    public boolean unregister(UUID userId) {
        if (!emitters.containsKey(userId)) {
            log.debug("Cannot unregister: user {} has no registered emitter", userId);
            return false;
        }
        emitters.remove(userId);
        return true;
    }

    /**
     * Get an SSE emitter.
     *
     * @param userId User ID to get SSE emitter for.
     * @return SSE emitter.
     */
    public SseEmitter get(UUID userId) {
        return emitters.get(userId);
    }


    /**
     * Check if the user has an SSE emitter registered.
     *
     * @param userId The user ID to check.
     * @return Whether the user has an SSE emitter registered.
     */
    public boolean isRegistered(UUID userId) {
        return emitters.containsKey(userId);
    }

    /**
     * Send a message to a single user.
     *
     * @param userId User ID to send the message to.
     * @param message Message to send.
     * @return Whether the message was successfully sent or not.
     * @throws IOException If the message could not be sent.
     */
    public boolean send(UUID userId, Object message) throws IOException {
        if (!emitters.containsKey(userId)) {
            log.debug("Cannot send message: user {} has no registered emitter", userId);
            return false;
        }
        emitters.get(userId).send(message);
        return true;
    }

    /**
     * Send a message to multiple users.
     *
     * @param users User IDs to send the message to.
     * @param message Message to send.
     * @return Whether the message was sent to all specified users or not.
     * @throws IOException If the message could not be sent.
     */
    public boolean send(Iterable<UUID> users, Object message) throws IOException {
        boolean success = true;
        for (UUID userId : users) {
            success &= send(userId, message);
        }
        return success;
    }

    /**
     * Send a message to all users.
     *
     * @param message Message to send.
     * @throws IOException If the message could not be sent.
     */
    public void sendAll(Object message) throws IOException {
        for (SseEmitter emitter : emitters.values()) {
            emitter.send(message);
        }
    }
}

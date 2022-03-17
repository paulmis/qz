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
@Slf4j
public class SSEManager {
    /**
     * The Map which maps user IDs to SSE emitters.
     * As this class can be called from different threads, we need to use a concurrent map.
     */
    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Get the number of registered SSE emitters.
     *
     * @return The number of registered SSE emitters.
     */
    public int size() {
        return emitters.size();
    }

    /**
     * Add a new SSE emitter.
     *
     * @param userId User ID to add SSE emitter for.
     */
    public void register(UUID userId, SseEmitter emitter) {
        if (emitters.containsKey(userId)) {
            emitters.get(userId).complete();
            unregister(userId);
            log.debug("Removed existing SSE emitter for user {}", userId);
        }

        emitters.put(userId, emitter);
    }

    /**
     * Remove an SSE emitter.
     *
     * @param userId User ID to remove SSE emitter for.
     * @return Whether the SSE emitter was successfully removed or not.
     */
    public boolean unregister(UUID userId) {
        return emitters.remove(userId) != null;
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

    /**
     * Disconnects the user's SSE emitter.
     *
     * @param userId user's id
     * @return whether the emitter was successfully disconnected or not
     */
    public boolean disconnect(UUID userId) {
        // If the user has no registered SSE emitter, we can't disconnect it.
        if (!isRegistered(userId)) {
            return false;
        }

        // Completes the emitter lifecycle and unregisters it.
        emitters.get(userId).complete();
        return unregister(userId);
    }

    /**
     * Disconnect all registered SSE emitters and clear the map.
     */
    public void disconnectAll() {
        for (SseEmitter emitter : emitters.values()) {
            emitter.complete();
        }
        emitters.clear();
    }
}

package server.services;

import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import server.api.exceptions.SSEFailedException;
import server.utils.SSE;

/**
 * Manager class to handle SSE emitters.
 */
@Slf4j
@Service
public class SSEManager {
    private final Counter sseMessageCounter;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    /**
     * Create a new SSE manager.
     *
     * @param registry Metrics registry.
     */
    @Autowired
    public SSEManager(MeterRegistry registry) {
        sseMessageCounter = Counter.builder("quiz_sse_messages")
                .tag("type", "sent")
                .description("Number of sent SSE messages")
                .register(registry);
        Gauge.builder("quiz_sse_emitters", this, SSEManager::size)
                .description("Number of registered SSE emitters")
                .register(registry);
    }

    /**
     * The Map which maps user IDs to SSE emitters.
     * As this class can be called from different threads, we need to use a concurrent map.
     */
    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Initialization routine for the SSE manager.
     */
    @PostConstruct
    public void init() {
        taskScheduler.scheduleAtFixedRate(this::sendKeepAlive, Duration.ofSeconds(45));
        log.info("Initialized SSE manager");
    }

    /**
     * Send a keep alive message to all registered SSE emitters.
     */
    public void sendKeepAlive() {
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(new SSEMessage(SSEMessageType.KEEPALIVE));
                log.trace("Sent keep alive to user {}", userId);
            } catch (IOException e) {
                log.error("Failed to send keep alive message to user {}", userId, e);
            }
        });
    }

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
            log.debug("Removed the previous SSE emitter for user [{}]", userId);
        }

        log.debug("Registered SSE emitter for user {}", userId);
        emitters.put(userId, emitter);
        log.info("Registered SSE emitter for user [{}]", userId);
    }

    /**
     * Remove an SSE emitter.
     *
     * @param userId User ID to remove SSE emitter for.
     * @return Whether the SSE emitter was successfully removed or not.
     */
    public boolean unregister(UUID userId) {
        if (!emitters.containsKey(userId)) {
            log.debug("Cannot unregister emitter: user {} has no registered emitter", userId);
            return false;
        } else {
            emitters.remove(userId);
            log.debug("Unregistered SSE emitter for user {}", userId);
            return true;
        }
    }

    /**
     * Safely removes an SSE emitter, verifying that the specific instance is the one removed, and not
     * any emitter assigned to the user.
     *
     * @param userId the id of the user
     * @param emitter the emitter to remove
     * @return whether the SSE emitter was successfully removed or not
     */
    public boolean unregister(UUID userId, SseEmitter emitter) {
        if (emitters.get(userId) == emitter) {
            emitters.remove(userId);
            log.debug("Unregistered SSE emitter for user {}", userId);
            return true;
        }

        log.debug("Cannot unregister emitter: user {} has no registered emitter", userId);
        return false;
    }

    /**
     * Get an SSE emitter.
     *
     * @param userId User ID to get SSE emitter for.
     * @return SSE emitter.
     */
    public SseEmitter get(UUID userId) {
        log.trace("Getting SSE emitter for user {}", userId);
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
     * @throws SSEFailedException If the message could not be sent.
     */
    public boolean send(UUID userId, SseEmitter.SseEventBuilder message) {
        // Check that the emitter for the user exists
        if (!emitters.containsKey(userId)) {
            log.debug("Cannot send message: user {} has no registered emitter", userId);
            return false;
        }

        // Send the message
        try {
            emitters.get(userId).send(message);
            sseMessageCounter.increment();
            log.debug("Sent message to user {}", userId);
            return true;
        } catch (IOException e) {
            log.error("Failed to send message to user {}", userId);
            return false;
        }
    }

    /**
     * Send a message to a single user.
     *
     * @param userId User ID to send the message to.
     * @param message Message to send.
     * @return Whether the message was successfully sent or not.
     */
    public boolean send(UUID userId, SSEMessage message) {
        log.trace("Sending message of type {} to user {}", message.getType(), userId);
        return send(userId, SSE.createEvent(message));
    }

    /**
     * Send a message to multiple users.
     *
     * @param users User IDs to send the message to.
     * @param message Message to send.
     * @return Whether the message was sent to all specified users or not.
     */
    public boolean send(Iterable<UUID> users, SseEmitter.SseEventBuilder message) {
        boolean success = true;
        for (UUID userId : users) {
            success &= send(userId, message);
        }
        return success;
    }

    /**
     * Send a message to multiple users.
     *
     * @param users User IDs to send the message to.
     * @param message Message to send.
     * @return Whether the message was sent to all specified users or not.
     */
    public boolean send(Iterable<UUID> users, SSEMessage message) {
        log.trace(String.valueOf(message));
        return send(users, SSE.createEvent(message));
    }

    /**
     * Send a message to all users.
     *
     * @param message Message to send.
     */
    public void sendAll(SseEmitter.SseEventBuilder message) {
        for (SseEmitter emitter : emitters.values()) {
            try {
                emitter.send(message);
            } catch (IOException e) {
                log.error("Failed to send sse message: " + e.getMessage());
            }
        }
    }

    /**
     * Send a message to all users.
     *
     * @param message Message to send.
     */
    public void sendAll(SSEMessage message) {
        log.trace("Sending message of type {} to all users", message.getType());
        sendAll(SSE.createEvent(message));
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
            log.debug("Cannot disconnect user {}: no registered emitter", userId);
            return false;
        }

        // Completes the emitter lifecycle and unregisters it.
        emitters.get(userId).complete();
        log.trace("Disconnected emitter for user {}", userId);
        return unregister(userId);
    }

    /**
     * Disconnect multiple users' SSE emitters.
     *
     * @param users user ids.
     * @return whether all the users were successfully disconnected or not.
     */
    public boolean disconnect(Iterable<UUID> users) {
        boolean success = true;
        for (UUID userId : users) {
            success &= disconnect(userId);
        }
        return success;
    }

    /**
     * Disconnect all registered SSE emitters and clear the map.
     */
    public void disconnectAll() {
        for (SseEmitter emitter : emitters.values()) {
            emitter.complete();
        }
        emitters.clear();

        log.trace("Disconnected all emitters");
    }
}

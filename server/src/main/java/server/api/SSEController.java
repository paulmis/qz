package server.api;

import commons.entities.game.GameStatus;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import java.io.IOException;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import server.database.entities.User;
import server.database.entities.auth.config.AuthContext;
import server.database.entities.game.Game;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameRepository;
import server.services.SSEManager;

/**
 * The SSE endpoint - opens new SSE connections.
 */
@RestController
@RequestMapping("/api/sse")
@Slf4j
public class SSEController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private SSEManager sseManager;

    /**
     * Open a new SSE connection.
     *
     * @return the response entity.
     */
    @GetMapping("/open")
    public ResponseEntity<SseEmitter> open() {
        // Create a new SSE emitter
        var emitter = new SseEmitter(Long.MAX_VALUE);
        try {
            // Get current user
            User user = userRepository.findByEmail(AuthContext.get())
                    .orElseThrow(() -> new NoSuchElementException("User not found"));
            // The user must currently be in a game
            gameRepository.getPlayersLobbyOrGame(user.getId())
                    .orElseThrow(() -> new IllegalStateException("User not in a game"));

            // Register emitter callbacks.
            emitter.onCompletion(() -> sseManager.unregister(user.getId()));
            emitter.onTimeout(() -> {
                log.warn("SSE connection timed out");
                sseManager.unregister(user.getId());
            });
            emitter.onError(throwable -> {
                log.error("Error in SSE connection", throwable);
                sseManager.unregister(user.getId());
            });

            // Register emitter to the SSE manager.
            sseManager.register(user.getId(), emitter);

            // The client will wait for a first event in order to start.
            sseManager.send(user.getId(), new SSEMessage(SSEMessageType.INIT));

            return ResponseEntity.ok(emitter);
        } catch (NoSuchElementException e) {
            // This should never happen
            log.error("Failed to fetch user information: " + e.getMessage());
            // If for some reason we cannot fetch user information, close the emitter
            emitter.complete();
            return ResponseEntity.internalServerError().body(emitter);
        } catch (IllegalStateException e) {
            log.debug("Failed to create SSE connection: " + e.getMessage());
            // Try to notify the user that they are not in a game.
            try {
                emitter.send(e.getMessage());
            } catch (IOException e1) {
                log.error("Failed to send error message to client: " + e1.getMessage());
            }
            emitter.complete();
            return ResponseEntity.badRequest().body(emitter);
        } catch (IOException e) {
            log.debug("Failed to create SSE connection: " + e.getMessage());
            return ResponseEntity.badRequest().body(emitter);
        }
    }
}

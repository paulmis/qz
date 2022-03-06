package server.api;

import commons.entities.game.GameStatus;
import java.security.Principal;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import server.database.entities.User;
import server.database.entities.game.Game;
import server.database.repositories.UserRepository;

/**
 * The SSE endpoint - opens new SSE connections.
 */
@RestController
@RequestMapping("/api/sse")
@Slf4j
public class SSEController {
    @Autowired
    private UserRepository userRepository;

    /**
     * Open a new SSE connection.
     *
     * @param principal the security principal of current user.
     * @return the response entity.
     */
    @GetMapping("/open")
    public ResponseEntity<SseEmitter> open(Principal principal) {
        // Create a new SSE emitter
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        try {
            // Get current user
            User user = userRepository.findByEmail(principal.getName()).orElseThrow(NoSuchElementException::new);

            // The user must currently be in a game
            Game game = user.getGamePlayers().stream().filter(gp -> gp.getGame().getStatus() == GameStatus.ONGOING)
                    .findFirst().orElseThrow(IllegalStateException::new).getGame();

            // Register emitter callbacks.
            emitter.onCompletion(() -> game.emitters.unregister(user.getId()));
            emitter.onTimeout(() -> {
                log.warn("SSE connection timed out");
                game.emitters.unregister(user.getId());
            });
            emitter.onError(throwable -> {
                log.error("Error in SSE connection", throwable);
                game.emitters.unregister(user.getId());
            });

            // Register emitter to the SSE manager.
            game.emitters.register(user.getId(), emitter);

            return ResponseEntity.ok(emitter);
        } catch (NoSuchElementException e) {
            log.error("Failed to fetch user information: " + e.getMessage());
            // If for some reason we cannot fetch user information, close the emitter
            emitter.complete();
            return ResponseEntity.internalServerError().body(emitter);
        } catch (IllegalStateException e) {
            log.debug("Failed to register emitter: user is not in the game");
            emitter.complete();
            return ResponseEntity.badRequest().body(emitter);
        }
    }
}

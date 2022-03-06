package server.api;

import java.util.UUID;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Endpoint to subscribe to SSE streams.
 */
@RestController
@RequestMapping("/api/sse")
public class SseController {

    /**
     * Endpoint to open an SSE connection.
     *
     * @param userId UUID of connecting user.
     * @return SSE emitter.
     */
    @PostMapping("/subscribe/{userId}")
    SseEmitter getSseListener(@PathVariable @NonNull UUID userId) {
        SseEmitter eventEmitter = new SseEmitter(-1L);
        // ToDo make eventEmitter available to game logic
        // Call register(userId, eventEmitter) from the instance of SSEManager
        return eventEmitter;
    }

    /**
     * Endpoint to close an SSE connection.
     *
     * @param userId UUID of disconnecting user.
     * @return HTTP status 200 if successful, 404 otherwise.
     */
    @PostMapping("/unsubscribe/{userId}")
    ResponseEntity closeSseListener(@PathVariable @NonNull UUID userId) {
        /*
        if(unregister(userId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
         */
        return null;
    }
}

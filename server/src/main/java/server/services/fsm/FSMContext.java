package server.services.fsm;

import lombok.Data;
import lombok.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import server.services.GameService;
import server.services.SSEManager;

/**
 * Execution context for the finite state machine.
 */
@Data
public class FSMContext {
    @NonNull private SSEManager sseManager;
    @NonNull private GameService gameService;
    @NonNull private ThreadPoolTaskScheduler taskScheduler;
}

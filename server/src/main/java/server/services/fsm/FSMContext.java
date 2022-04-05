package server.services.fsm;

import lombok.Data;
import lombok.Generated;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import server.configuration.quiz.QuizConfiguration;
import server.database.repositories.game.GameRepository;
import server.services.GameService;
import server.services.SSEManager;

/**
 * Execution context for the finite state machine.
 */
@Data
@Generated
public class FSMContext {
    @Getter @NonNull private GameService gameService;

    /**
     * Get the {@link SSEManager} of the execution context.
     *
     * @return The {@link SSEManager} used to manage SSE emitters.
     */
    public SSEManager getSseManager() {
        return gameService.getSseManager();
    }

    /**
     * Get the {@link ThreadPoolTaskScheduler} of the execution context.
     *
     * @return The {@link ThreadPoolTaskScheduler} used to schedule futures.
     */
    public ThreadPoolTaskScheduler getTaskScheduler() {
        return gameService.getTaskScheduler();
    }

    /**
     * Get the {@link QuizConfiguration} of the execution context.
     *
     * @return The {@link QuizConfiguration} of the execution context.
     */
    public QuizConfiguration getQuizConfiguration() {
        return gameService.getQuizConfiguration();
    }

    public GameRepository getRepository() {
        return gameService.getGameRepository();
    }
}

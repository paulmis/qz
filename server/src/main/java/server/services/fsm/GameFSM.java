package server.services.fsm;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import server.database.entities.game.Game;
import server.services.GameService;

/**
 * The GameFSM class is a Finite State Machine that is used to manage a game.
 */
@Slf4j
@Data
public abstract class GameFSM {
    /**
     * Game that is being managed.
     */
    @NonNull protected Game game;

    /**
     * Execution context of the FSM.
     */
    @NonNull private final FSMContext context;

    /**
     * Current state of the FSM.
     */
    private FSMState state = FSMState.IDLE;

    /**
     * Indicates whether the FSM is currently running.
     */
    private boolean running = false;

    /**
     * The next state to transition to.
     */
    private FSMFuture future;

    /**
     * Schedule a task to run after a delay.
     *
     * @param task Task to execute.
     * @param delay Delay before executing the task.
     */
    public void scheduleTask(Runnable task, Duration delay) {
        log.debug("[{}] Scheduling task in {}", game.getId(), delay);
        Date executionTime = Date.from(Instant.now().plus(delay));
        setFuture(new FSMFuture(
                Optional.of(context.getTaskScheduler().schedule(task, executionTime)),
                executionTime, task));
    }

    /**
     * Reprograms the current task to a different time.
     *
     * @param delay the new delay.
     */
    public void reprogramCurrentTask(Duration delay) {
        log.debug("[{}] Reprogramming task in {}", game.getId(), delay);
        Date executionTime = Date.from(Instant.now().plus(delay));

        if (future.getFuture().isPresent()) {
            future.getFuture().get().cancel(true);
            setFuture(new FSMFuture(
                    Optional.of(context.getTaskScheduler().schedule(future.getRunnable(), executionTime)),
                    executionTime, future.getRunnable()));
        }
    }

    /**
     * Stop the finite state machine.
     */
    public void stop() {
        log.debug("[{}] Stopping FSM", game.getId());
        setRunning(false);
    }

    /**
     * Check whether the FSM can be started.
     *
     * @return Whether the FSM can be started or not.
     */
    public boolean isStartable() {
        return state == FSMState.IDLE;
    }

    /**
     * Run the finite state machine.
     */
    public abstract void run();

    public void refreshGame() {
        game = context.getRepository().findById(game.getId()).get();
    }
}
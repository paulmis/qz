package server.services.fsm;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import lombok.Data;
import lombok.NonNull;
import server.database.entities.game.Game;

/**
 * The GameFSM class is a Finite State Machine that is used to manage a game.
 */
@Data
public abstract class GameFSM {
    /**
     * Game that is being managed.
     */
    @NonNull private final Game game;

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
        Date executionTime = Date.from(Instant.now().plus(delay));
        setFuture(new FSMFuture(
                Optional.of(context.getTaskScheduler().schedule(task, executionTime)),
                executionTime));
    }

    /**
     * Run the finite state machine.
     */
    public abstract void run();
}
package server.services.fsm;

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
     * Indicates whether the FSM is currently running.
     */
    private boolean running = false;

    /**
     * The next state to transition to.
     */
    private FSMFuture future;

    /**
     * Run the finite state machine.
     */
    public abstract void run();
}
package server.services.fsm;

import lombok.Data;
import lombok.NonNull;
import server.database.entities.game.Game;

/**
 * The GameFSM class is a Finite State Machine that is used to manage a game.
 */
@Data
public abstract class GameFSM implements Runnable {
    @NonNull private final Game game;

    @NonNull private final FSMContext context;

    private FSMFuture future;

    /**
     * Run the finite state machine.
     */
    @Override
    public abstract void run();
}
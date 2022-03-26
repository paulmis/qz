package server.services;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import server.database.entities.game.Game;
import server.services.fsm.GameFSM;

/**
 * Class for managing the finite state machines.
 */
@Slf4j
@Service
public class FSMManager {
    private final ConcurrentHashMap<UUID, GameFSM> fsmMap = new ConcurrentHashMap<>();

    /**
     * Adds a new finite state machine to the manager.
     *
     * @param fsm the finite state machine to add
     */
    public void addFSM(Game game, GameFSM fsm) {
        fsmMap.put(game.getId(), fsm);
    }

    /**
     * Removes a finite state machine from the manager.
     *
     * @param game the game to remove
     * @return the removed finite state machine.
     */
    public GameFSM removeFSM(Game game) {
        return fsmMap.remove(game.getId());
    }

    /**
     * Start the finite state machine for the given game.
     *
     * @param game the game to get the finite state machine for.
     * @return whether the finite state machine was started.
     * @throws IllegalStateException if the finite state machine is already running.
     */
    public boolean startFSM(Game game) {
        // Check if the game contains a finite state machine
        if (fsmMap.containsKey(game.getId())) {
            GameFSM fsm = fsmMap.get(game.getId());
            // Check if the finite state machine is already running
            if (!fsm.isRunning() && fsm.isStartable()) {

                log.debug("Starting FSM for game {}", game.getId());
                // Start the finite state machine (in a new thread, to avoid blocking the main thread)
                new Thread(fsm::run).start();
                return true;

            } else {
                log.warn("FSM for game {} is already running or not startable", game.getId());
                return false;
            }
        }
        log.warn("FSM for game {} not found", game.getId());
        return false;
    }

    /**
     * Stop the finite state machine for the given game.
     *
     * @param game the game to stop the finite state machine for.
     * @return whether the finite state machine was stopped.
     */
    public boolean stopFSM(Game game) {
        // Check if the game contains a finite state machine
        if (fsmMap.containsKey(game.getId())) {
            GameFSM fsm = fsmMap.get(game.getId());
            // Check if the finite state machine is running
            if (fsm.isRunning()) {
                log.debug("Stopping FSM for game {}", game.getId());
                // Stop the finite state machine
                fsm.stop();
                return true;
            } else {
                log.trace("FSM for game {} not running", game.getId());
                return false;
            }

        }
        log.trace("FSM for game {} not found", game.getId());
        return false;
    }

    /**
     * Get the size of the FSM map.
     *
     * @return the size of the FSM map.
     */
    public int size() {
        return fsmMap.size();
    }
}
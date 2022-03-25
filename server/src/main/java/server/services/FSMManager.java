package server.services;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.entities.game.Game;
import server.services.fsm.GameFSM;

/**
 * Class for managing the finite state machines.
 */
@Service
@Slf4j
public class FSMManager {
    @Autowired
    private SSEManager sseManager;

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
        if (fsmMap.containsKey(game.getId())) {
            GameFSM fsm = fsmMap.get(game.getId());
            if (!fsm.isRunning()) {
                log.debug("Starting FSM for game {}", game.getId());
                // Start the finite state machine (in a new thread, to avoid blocking the main thread)
                new Thread(fsm::run).start();
                return true;
            } else {
                log.warn("FSM for game {} already running", game.getId());
                throw new IllegalStateException("FSM for game " + game.getId() + " already running");
            }
        }
        log.warn("FSM for game {} not found", game.getId());
        return false;
    }
}
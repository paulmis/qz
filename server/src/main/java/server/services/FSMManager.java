package server.services;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import lombok.NonNull;
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
    SSEManager sseManager;

    @Data
    static class FSMEntry {
        @NonNull GameFSM fsm;
        Thread thread;
    }

    private final ConcurrentHashMap<UUID, FSMEntry> fsmMap = new ConcurrentHashMap<>();

    /**
     * Adds a new finite state machine to the manager.
     *
     * @param fsm the finite state machine to add
     */
    public void addFSM(Game game, GameFSM fsm) {
        fsmMap.put(game.getId(), new FSMEntry(fsm));
    }

    /**
     * Removes a finite state machine from the manager.
     *
     * @param game the game to remove
     * @return the removed finite state machine.
     */
    public GameFSM removeFSM(Game game) {
        return fsmMap.remove(game.getId()).getFsm();
    }

    /**
     * Start the finite state machine for the given game.
     *
     * @param game the game to get the finite state machine for.
     * @return whether the finite state machine was started.
     */
    public boolean startFSM(Game game) {
        if (fsmMap.containsKey(game.getId())) {
            FSMEntry entry = fsmMap.get(game.getId());
            if (entry.thread == null || !entry.thread.isAlive()) {
                log.debug("Starting FSM for game {}", game.getId());
                entry.thread = new Thread(entry.fsm);
                entry.thread.start();
                return true;
            } else {
                log.warn("FSM for game {} already running", game.getId());
                return false;
            }
        }
        log.warn("FSM for game {} not found", game.getId());
        return false;
    }
}
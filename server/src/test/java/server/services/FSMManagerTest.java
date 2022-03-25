package server.services;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static server.utils.TestHelpers.getUUID;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import server.database.entities.game.NormalGame;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.services.fsm.DefiniteGameFSM;
import server.services.fsm.FSMContext;
import server.utils.FSMHelpers;

@ExtendWith(MockitoExtension.class)
class FSMManagerTest {
    private FSMManager fsmManager;

    private NormalGame game;

    @Mock
    private SSEManager sseManager;
    @Mock
    private GameService gameService;
    @Mock
    private ThreadPoolTaskScheduler taskScheduler;

    @InjectMocks
    private FSMContext context;

    @BeforeEach
    void setUp() {
        fsmManager = new FSMManager();
        game = new NormalGame();
        game.setId(getUUID(1));

        NormalGameConfiguration config = new NormalGameConfiguration();
        config.setNumQuestions(10);
        game.setConfiguration(config);

        lenient().when(taskScheduler.schedule(any(), any(Date.class))).thenReturn(new FSMHelpers.MockFuture<>());
    }

    @Test
    void addFSM() {
        DefiniteGameFSM fsm = new DefiniteGameFSM(game, context);
        assertEquals(0, fsmManager.size());
        fsmManager.addFSM(game, fsm);
        assertEquals(1, fsmManager.size());
    }

    @Test
    void removeFSM() {
        DefiniteGameFSM fsm = new DefiniteGameFSM(game, context);
        fsmManager.addFSM(game, fsm);
        assertEquals(1, fsmManager.size());
        fsmManager.removeFSM(game);
        assertEquals(0, fsmManager.size());
    }

    @Test
    void startFSM() throws InterruptedException {
        DefiniteGameFSM fsm = new DefiniteGameFSM(game, context);

        // Add a new FSM
        fsmManager.addFSM(game, fsm);
        // Verify that it's not running
        assertFalse(fsm.isRunning());

        // Start the FSM
        assertTrue(fsmManager.startFSM(game));
        await().atMost(250, TimeUnit.MILLISECONDS).until(fsm::isRunning);
        // Verify that it's running
        assertTrue(fsm.isRunning());
    }

    @Test
    void startFSMAlreadyStarted() throws InterruptedException {
        DefiniteGameFSM fsm = new DefiniteGameFSM(game, context);

        // Add a new FSM
        fsmManager.addFSM(game, fsm);
        // Start the FSM
        assertTrue(fsmManager.startFSM(game));
        await().atMost(250, TimeUnit.MILLISECONDS).until(fsm::isRunning);
        // Verify that it's running
        assertTrue(fsm.isRunning());
        // Try to start it again
        assertThrows(IllegalStateException.class, () -> fsmManager.startFSM(game));
        // Verify that it's still running
        assertTrue(fsm.isRunning());
    }

    @Test
    void startFSMNotFound() {
        assertFalse(fsmManager.startFSM(game));
    }

    @Test
    void size() {
        assertEquals(0, fsmManager.size());
        fsmManager.addFSM(game, new DefiniteGameFSM(game, context));
        assertEquals(1, fsmManager.size());
    }
}
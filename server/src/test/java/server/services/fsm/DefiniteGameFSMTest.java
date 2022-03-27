package server.services.fsm;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static server.utils.TestHelpers.getUUID;

import commons.entities.game.GameStatus;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import server.database.entities.game.DefiniteGame;
import server.database.entities.game.Game;
import server.database.entities.game.NormalGame;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.services.GameService;
import server.services.SSEManager;
import server.utils.FSMHelpers;

@ExtendWith(MockitoExtension.class)
class DefiniteGameFSMTest {
    private DefiniteGame game;
    private NormalGameConfiguration configuration;

    @Captor
    private ArgumentCaptor<Boolean> booleanCaptor;
    @Captor
    private ArgumentCaptor<SSEMessage> sseMessageCaptor;
    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;

    @Mock
    private SSEManager sseManager;
    @Mock
    private GameService gameService;
    @Mock
    private ThreadPoolTaskScheduler taskScheduler;

    @InjectMocks
    private FSMContext context;

    @BeforeEach
    void setUp() throws IOException {
        game = new NormalGame();
        game.setId(getUUID(1));
        configuration = new NormalGameConfiguration();
        configuration.setNumQuestions(10);
        game.setConfiguration(configuration);
        game.setCurrentQuestionNumber(0);
        game.setStatus(GameStatus.ONGOING);

        lenient().when(sseManager.send(any(UUID.class), any(SSEMessage.class))).thenReturn(true);
        lenient().when(taskScheduler.schedule(any(), any(Date.class))).thenReturn(new FSMHelpers.MockFuture<>());
    }

    @Test
    void constructorTestNotYetStarted() {
        game.setStatus(GameStatus.CREATED);
        // We should not be able to spawn a new game from a finished game
        assertThrows(IllegalStateException.class, () -> new DefiniteGameFSM(game, context));
    }

    @Test
    void constructorTestFinishedGame() {
        game.setStatus(GameStatus.FINISHED);
        // We should not be able to spawn a new game from a finished game
        assertThrows(IllegalStateException.class, () -> new DefiniteGameFSM(game, context));
    }

    @Test
    void run() {
        DefiniteGameFSM fsm = new DefiniteGameFSM(game, context);
        fsm.run();

        // Verify that the game is in the correct state
        assertEquals(FSMState.PREPARING, fsm.getState());
    }


    @Test
    void runLeaderboard() throws IOException {
        DefiniteGameFSM fsm = new DefiniteGameFSM(game, context);
        fsm.setRunning(true);
        // Force the game to be in the leaderboard state
        fsm.runLeaderboard();

        // Verify that the users are notified of the leaderboard
        verify(sseManager, times(1)).send(any(Iterable.class), sseMessageCaptor.capture());
        assertEquals(SSEMessageType.SHOW_LEADERBOARD, sseMessageCaptor.getValue().getType());
    }

    @Test
    void runAnswerToLeaderboard() throws IOException {
        // Put the game in a state where leaderboard should be shown
        game.setCurrentQuestionNumber(4);
        DefiniteGameFSM fsm = new DefiniteGameFSM(game, context);
        fsm.setRunning(true);
        fsm.runAnswer();

        // Verify that the transition to leaderboard happens
        verify(taskScheduler, times(1)).schedule(runnableCaptor.capture(), any(Date.class));
        runnableCaptor.getValue().run();

        // Verify that the leaderboard notification is sent.
        verify(sseManager, times(1)).send(any(Iterable.class), sseMessageCaptor.capture());
        assertEquals(SSEMessageType.SHOW_LEADERBOARD, sseMessageCaptor.getValue().getType());
    }

    @Test
    void runAnswerGameStopped() {
        DefiniteGameFSM fsm = new DefiniteGameFSM(game, context);
        fsm.runAnswer();
        // Verify that the function does not continue normally.
        verifyNoMoreInteractions(context.getGameService());
    }

    @Test
    void runQuestionGameStopped() {
        DefiniteGameFSM fsm = new DefiniteGameFSM(game, context);
        fsm.runQuestion();
        // Verify that the function does not continue normally.
        verifyNoMoreInteractions(context.getGameService());
    }

    @Test
    void runLeaderboardGameStopped() {
        DefiniteGameFSM fsm = new DefiniteGameFSM(game, context);
        fsm.runLeaderboard();
        // Verify that the function does not continue normally.
        verifyNoMoreInteractions(context.getGameService());
    }
}
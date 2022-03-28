package server.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static server.utils.TestHelpers.getUUID;

import commons.entities.game.GameStatus;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import server.database.entities.User;
import server.database.entities.game.DefiniteGame;
import server.database.entities.game.GamePlayer;
import server.database.entities.game.MockGame;
import server.database.entities.game.NormalGame;
import server.database.entities.game.configuration.MockGameConfiguration;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.database.entities.game.exceptions.GameFinishedException;
import server.database.entities.game.exceptions.LastPlayerRemovedException;
import server.database.entities.question.MCQuestion;
import server.database.entities.question.Question;
import server.database.repositories.game.GameRepository;
import server.database.repositories.question.QuestionRepository;

/**
 * Tests for GameService class.
 */
@ExtendWith(MockitoExtension.class)
public class GameServiceTest {
    @Mock
    SSEManager sseManager;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private FSMManager fsmManager;

    @Mock
    private ThreadPoolTaskScheduler taskScheduler;

    @InjectMocks
    private GameService gameService;

    NormalGame game;
    User joe;
    User susanne;
    User james;
    GamePlayer joePlayer;
    GamePlayer susannePlayer;
    GamePlayer jamesPlayer;
    MCQuestion questionA;
    MCQuestion questionB;
    MCQuestion questionC;
    MCQuestion questionD;
    List<UUID> usedQuestionIds;

    @BeforeEach
    void init() {
        // Create users
        joe = new User("joe", "joe@doe.com", "stinkywinky");
        joe.setId(getUUID(0));
        joePlayer = new GamePlayer(joe);
        joePlayer.setJoinDate(LocalDateTime.parse("2020-03-04T00:00:00"));

        susanne = new User("Susanne", "susanne@louisiane.com", "stinkymonkey");
        susanne.setId(getUUID(1));
        susannePlayer = new GamePlayer(susanne);
        susannePlayer.setJoinDate(LocalDateTime.parse("2022-03-03T00:00:00"));

        james = new User("James", "james@blames.com", "stinkydonkey");
        james.setId(getUUID(2));
        jamesPlayer = new GamePlayer(james);
        jamesPlayer.setJoinDate(LocalDateTime.parse("2022-03-02T00:00:00"));

        // Create questions
        questionA = new MCQuestion();
        questionA.setId(getUUID(5));
        questionB = new MCQuestion();
        questionB.setId(getUUID(6));
        questionC = new MCQuestion();
        questionC.setId(getUUID(7));
        questionD = new MCQuestion();
        questionD.setId(getUUID(8));
        usedQuestionIds = Arrays.asList(questionB.getId(), questionA.getId());

        // Create the game
        game = new NormalGame();
        game.setId(getUUID(3));
        game.setConfiguration(new NormalGameConfiguration(3, Duration.ofSeconds(13), 2, 2, 2f, 100, 0, 75));
        game.add(joePlayer);
        game.add(susannePlayer);

        // Mock the repository
        lenient().when(questionRepository.count()).thenReturn(4L);
        lenient().when(questionRepository.findAll())
                .thenReturn(Arrays.asList(questionA, questionC, questionB, questionD));
    }

    @Test
    void provideQuestionsOk() {
        // ToDo: fix QuestionRepository::findByIdNotIn
        // Mock the repository
        //when(questionRepository.findByIdNotIn(usedQuestionIds))
        //        .thenReturn(Arrays.asList(questionD, questionC));

        // Provide the questions
        List<Question> questions = gameService.provideQuestions(2, Arrays.asList(questionB, questionA));
        assertEquals(2, questions.size());
        assertThat(questions).hasSameElementsAs(Arrays.asList(questionC, questionD));

        // Verify interactions
        verify(questionRepository).count();
        // ToDo: fix QuestionRepository::findByIdNotIn
        //verify(questionRepository).findByIdNotIn(usedQuestionIds);
        verify(questionRepository).findAll();
        verifyNoMoreInteractions(questionRepository);
    }

    @Test
    void provideQuestionsNotEnough() {
        // Expect a throw
        assertThrows(IllegalStateException.class,
                () -> gameService.provideQuestions(3, Arrays.asList(questionB, questionA)));

        // Verify interactions
        verify(questionRepository).count();
        verifyNoMoreInteractions(questionRepository);
    }

    @Test
    void startNormal() throws IOException {
        // Mock the repository
        when(gameRepository.save(any(DefiniteGame.class))).thenReturn(game);

        // Start the game
        gameService.start(game);

        // Check that the questions have been generated and the status was changed
        assertEquals(3, game.getQuestions().size());
        assertEquals(GameStatus.ONGOING, game.getStatus());
        assertNull(game.getCurrentQuestionNumber());

        // Verify interactions
        verify(questionRepository).count();
        // ToDo: fix QuestionRepository::findByIdNotIn
        //verify(questionRepository).findByIdNotIn(new ArrayList<>());
        verify(questionRepository).findAll();
        verify(sseManager, times(1)).send(any(Iterable.class), any(SSEMessage.class));
        verifyNoMoreInteractions(questionRepository, sseManager);
    }

    @Test
    void startOngoing() {
        // Set the mocked game to be already started
        game.setStatus(GameStatus.ONGOING);

        // Start the game
        assertThrows(IllegalStateException.class, () -> gameService.start(game));

        // Verify interactions
        verifyNoMoreInteractions(questionRepository);
    }

    @Test
    void startNotFull() throws LastPlayerRemovedException {
        // Remove susanne
        game.remove(susanne.getId());

        // Start the game
        assertThrows(IllegalStateException.class, () -> gameService.start(game));

        // Verify interactions
        verifyNoMoreInteractions(questionRepository);
    }

    @Test
    void startUnsupported() {
        // Create a mock game
        MockGame mockGame = new MockGame();
        MockGameConfiguration mockGameConfiguration =
            new MockGameConfiguration(Duration.ofSeconds(13), 1, 2, 2f, 100, 0, 75);
        mockGame.setId(getUUID(6));
        mockGame.setConfiguration(mockGameConfiguration);
        mockGame.add(joePlayer);

        // Call the service function
        assertThrows(NotImplementedException.class, () -> gameService.start(mockGame));

        // Verify that the game hasn't been started
        assertEquals(GameStatus.CREATED, game.getStatus());

        // Verify interactions
        verifyNoMoreInteractions(gameRepository, sseManager);
    }

    @Test
    void removePlayerOk() throws IOException {
        // Set status and then call the service to remove joe
        game.setStatus(GameStatus.ONGOING);
        assertDoesNotThrow(() -> gameService.removePlayer(game, joe));

        // Verify that the status hasn't changed
        assertEquals(GameStatus.ONGOING, game.getStatus());

        // Verify interactions
        verify(sseManager, times(1)).unregister(joe.getId());
        verify(sseManager, times(1)).send(any(Iterable.class), any(SSEMessage.class));
        verifyNoMoreInteractions(sseManager);
    }

    @Test
    void removePlayerLast() throws LastPlayerRemovedException, IOException {
        // Remove susanne and then call the service to remove joe
        game.setStatus(GameStatus.ONGOING);
        game.remove(susanne.getId());
        assertDoesNotThrow(() -> gameService.removePlayer(game, joe));

        // Verify that the status changed
        assertEquals(GameStatus.FINISHED, game.getStatus());
        assertEquals(0, game.size());

        // Verify interactions
        verify(sseManager, times(1)).unregister(joe.getId());
        verify(sseManager, times(1)).send(any(Iterable.class), any(SSEMessage.class));
        verifyNoMoreInteractions(sseManager);
    }

    @Test
    void removePlayerNotFound() {
        // Remove a player that is not in the game
        game.setStatus(GameStatus.ONGOING);
        assertThrows(IllegalStateException.class, () -> gameService.removePlayer(game, james));

        // Verify that the status hasn't changed
        assertEquals(GameStatus.ONGOING, game.getStatus());
        assertEquals(2, game.getPlayers().size());

        // Verify interactions
        verifyNoMoreInteractions(sseManager);
    }

    @Test
    void nextQuestionOk() throws IOException {
        // Set the game to be started and mock the repository
        game.setStatus(GameStatus.ONGOING);
        when(gameRepository.save(game)).thenReturn(game);

        // Call the service
        Long delay = 1000L;
        gameService.nextQuestion(game, delay);

        // Check changes
        assertEquals(0, game.getCurrentQuestionNumber());
        assertTrue(game.isAcceptingAnswers());

        // Verify interactions
        verify(gameRepository, times(1)).save(game);
        verify(sseManager, times(1)).send(
            any(Iterable.class),
            eq(new SSEMessage(SSEMessageType.START_QUESTION, delay)));
        verifyNoMoreInteractions(gameRepository, sseManager);
    }

    @Test
    void nextQuestionFinished() {
        // Set the game to be started and set the current question to be the last one
        game.setStatus(GameStatus.ONGOING);
        game.setCurrentQuestionNumber(game.getConfiguration().getNumQuestions() - 1);

        // Call the service
        assertThrows(GameFinishedException.class, () -> gameService.nextQuestion(game, 0L));
    }

    @Test
    void showAnswerOk() throws IOException {
        // Set the game to be started and mock the repository
        game.setStatus(GameStatus.ONGOING);
        when(gameRepository.save(game)).thenReturn(game);


        // Call the service
        Long delay = 1000L;
        gameService.showAnswer(game, delay);

        // Check changes
        assertFalse(game.isAcceptingAnswers());

        // Verify interactions
        verify(gameRepository, times(1)).save(game);
        verify(sseManager, times(1)).send(
            any(Iterable.class),
            eq(new SSEMessage(SSEMessageType.STOP_QUESTION, delay)));
        verifyNoMoreInteractions(gameRepository, sseManager);
    }

    @Test
    void finishOk() throws IOException {
        // Set the game to be started and mock the repository
        game.setStatus(GameStatus.ONGOING);
        when(gameRepository.save(game)).thenReturn(game);

        // Call the service
        gameService.finish(game);

        // Check changes
        assertEquals(GameStatus.FINISHED, game.getStatus());

        // Verify interactions
        verify(gameRepository, times(1)).save(game);
        verify(sseManager, times(1)).send(
            any(Iterable.class),
            eq(new SSEMessage(SSEMessageType.GAME_END)));
        verifyNoMoreInteractions(gameRepository, sseManager);
    }
}

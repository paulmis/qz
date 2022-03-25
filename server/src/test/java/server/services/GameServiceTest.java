package server.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static server.utils.TestHelpers.getUUID;

import commons.entities.game.GameStatus;
import commons.entities.messages.SSEMessage;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import server.database.entities.User;
import server.database.entities.game.GamePlayer;
import server.database.entities.game.NormalGame;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.database.entities.game.exceptions.LastPlayerRemovedException;
import server.database.entities.question.MCQuestion;
import server.database.entities.question.Question;
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
        // ToDo: fix QuestionRepository::findByIdNotIn
        // Mock the repository
        //when(questionRepository.findByIdNotIn(new ArrayList<>()))
        //        .thenReturn(Arrays.asList(questionA, questionC, questionB, questionD));

        // Start the game
        gameService.startGame(game);

        // Check that the questions have been generated and the status was changed
        assertEquals(3, game.getQuestions().size());
        assertEquals(GameStatus.ONGOING, game.getStatus());
        assertEquals(0, game.getCurrentQuestionNumber());

        // Verify interactions
        verify(questionRepository).count();
        // ToDo: fix QuestionRepository::findByIdNotIn
        //verify(questionRepository).findByIdNotIn(new ArrayList<>());
        verify(questionRepository).findAll();
        verifyNoMoreInteractions(questionRepository);
        verify(sseManager, times(1)).send(any(Iterable.class), any(SSEMessage.class));
    }

    @Test
    void startOngoing() {
        // Set the mocked game to be already started
        game.setStatus(GameStatus.ONGOING);

        // Start the game
        assertThrows(IllegalStateException.class, () -> gameService.startGame(game));

        // Verify interactions
        verifyNoMoreInteractions(questionRepository);
    }

    @Test
    void startNotFull() throws LastPlayerRemovedException {
        // Remove susanne
        game.remove(susanne.getId());

        // Start the game
        assertThrows(IllegalStateException.class, () -> gameService.startGame(game));

        // Verify interactions
        verifyNoMoreInteractions(questionRepository);
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
    void setAcceptingAnswersTrue() throws IOException {
        game.setAcceptingAnswers(false);

        gameService.setAcceptingAnswers(game, true);

        assertTrue(game.isAcceptingAnswers());

        verify(sseManager, times(1)).send(any(Iterable.class), any(SSEMessage.class));
        verifyNoMoreInteractions(sseManager);
    }

    @Test
    void setAcceptingAnswersFalse() throws IOException {
        game.setAcceptingAnswers(true);

        gameService.setAcceptingAnswers(game, false);

        assertFalse(game.isAcceptingAnswers());

        verify(sseManager, times(1)).send(any(Iterable.class), any(SSEMessage.class));
        verifyNoMoreInteractions(sseManager);
    }

    @Test
    void setAcceptingAnswersDelayTrue() throws IOException {
        game.setAcceptingAnswers(false);

        gameService.setAcceptingAnswers(game, true, 1000L);

        assertTrue(game.isAcceptingAnswers());

        verify(sseManager, times(1)).send(any(Iterable.class), any(SSEMessage.class));
        verifyNoMoreInteractions(sseManager);
    }

    @Test
    void setAcceptingAnswersDelayFalse() throws IOException {
        game.setAcceptingAnswers(true);

        gameService.setAcceptingAnswers(game, false, 1000L);

        assertFalse(game.isAcceptingAnswers());

        verify(sseManager, times(1)).send(any(Iterable.class), any(SSEMessage.class));
        verifyNoMoreInteractions(sseManager);
    }
}

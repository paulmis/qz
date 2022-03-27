package server.database.entities.game;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static server.utils.TestHelpers.getUUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.entities.game.GameStatus;
import commons.entities.game.NormalGameDTO;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.entities.User;
import server.database.entities.answer.Answer;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.database.entities.game.exceptions.LastPlayerRemovedException;
import server.database.entities.question.Activity;
import server.database.entities.question.MCQuestion;
import server.database.repositories.question.QuestionRepository;
import server.services.GameService;
import server.services.SSEManager;

/**
 * Tests for NormalGame class.
 */
@ExtendWith(MockitoExtension.class)
public class NormalGameTest {
    NormalGame game;
    NormalGameConfiguration config;
    User joe;
    User susanne;
    GamePlayer joePlayer;
    GamePlayer susannePlayer;
    MCQuestion questionA;
    MCQuestion questionB;
    Activity activityA;
    Activity activityB;
    Activity activityC;
    Activity activityD;

    @BeforeEach
    void init() {
        // Create users
        joe = new User("joe", "joe@doe.com", "stinkywinky");
        joe.setId(getUUID(0));
        joePlayer = new GamePlayer(joe);
        joePlayer.setId(getUUID(1));
        joePlayer.setJoinDate(LocalDateTime.parse("2020-03-04T00:00:00"));

        susanne = new User("Susanne", "susanne@louisiane.com", "stinkymonkey");
        susanne.setId(getUUID(2));
        susannePlayer = new GamePlayer(susanne);
        susannePlayer.setId(getUUID(3));
        susannePlayer.setJoinDate(LocalDateTime.parse("2022-03-03T00:00:00"));

        // Create questions
        questionA = new MCQuestion();
        questionB = new MCQuestion();

        activityA = new Activity();
        activityA.setCost(100);

        activityB = new Activity();
        activityB.setCost(200);

        activityC = new Activity();
        activityC.setCost(300);

        activityD = new Activity();
        activityD.setCost(400);


        var activities = List.of(activityA, activityB, activityC, activityD);
        questionA.setActivities(activities);
        questionA.setAnswer(activityA);

        questionB.setActivities(activities);
        questionB.setAnswer(activityB);

        // Create config
        config = new NormalGameConfiguration(17, Duration.ofSeconds(13), 2, 2, 2f, 100, -10, 75);

        // Create the game
        game = new NormalGame();
        game.setId(getUUID(4));
        game.setConfiguration(config);
        game.addQuestions(Arrays.asList(questionA, questionB));
        game.add(joePlayer);
        game.add(susannePlayer);
    }

    @Test
    void convert() throws JsonProcessingException {
        // Create a ObjectMapper
        ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

        // Clear questions
        game.setQuestions(new ArrayList<>());

        // Serialize and deserialize
        String string = om.writeValueAsString(game.getDTO());
        NormalGameDTO dto = om.readValue(string, NormalGameDTO.class);

        // Initiate a copy of the game
        // Exclude players, questions, and host as the created lobby must be empty
        NormalGame repl = new NormalGame(dto);
        assertThat(game)
                .usingRecursiveComparison()
                .ignoringFields("players", "questions", "answers", "host", "random")
                .isEqualTo(repl);
    }

    @Test
    void getQuestionsCount() {
        assertEquals(17, game.getQuestionsCount());
    }

    @Test
    void getQuestionOk() {
        game.setCurrentQuestionNumber(1);
        assertEquals(Optional.of(questionB), game.getQuestion());
    }

    @Test
    void getQuestionOutOfBounds() {
        game.setCurrentQuestionNumber(2);
        assertEquals(Optional.empty(), game.getQuestion());
    }

    @Test
    void addAlreadyJoined() {
        assertFalse(game.add(joePlayer));
    }

    @Test
    void size() {
        joePlayer.setAbandoned(true);
        assertEquals(1, game.size());
    }

    @Test
    void isNotFull() {
        // Expand the lobby capacity and check that it is no longer full
        config.setCapacity(3);
        assertFalse(game.isFull());
    }

    @Test
    void isFull() {
        assertTrue(game.isFull());
    }

    @Test
    void removeLobbyOk() throws LastPlayerRemovedException {
        assertTrue(game.remove(susanne.getId()));
        assertFalse(game.getPlayers().containsKey(susanne.getId()));
    }

    @Test
    void removeNotFound() throws LastPlayerRemovedException {
        assertFalse(game.remove(getUUID(64)));
    }

    @Test
    void removeLobbyHead() throws LastPlayerRemovedException {
        assertTrue(game.remove(joe.getId()));
        assertEquals(game.getHost(), susannePlayer);
    }

    @Test
    void removeLastPlayer() {
        assertThrows(LastPlayerRemovedException.class, () -> {
            game.remove(joe.getId());
            game.remove(susanne.getId());
        });
    }

    @Test
    void removeGameOngoingOk() throws LastPlayerRemovedException {
        game.setStatus(GameStatus.ONGOING);
        assertTrue(game.remove(susanne.getId()));
        assertTrue(game.getPlayers().containsKey(susanne.getId()));
        assertTrue(game.getPlayers().get(susanne.getId()).isAbandoned());
    }

    @Test
    void removeGameFinished() throws LastPlayerRemovedException {
        game.setStatus(GameStatus.FINISHED);
        assertFalse(game.remove(joe.getId()));
    }

    @Test
    void computeBaseScoreNegative() {
        assertEquals(-10, game.computeBaseScore(0.0));
    }

    @Test
    void computeBaseScore100() {
        assertEquals(100, game.computeBaseScore(1.0));
    }

    @Test
    void computeBaseScoreIncreasing() {
        var scores = IntStream.range(0, 100)
                .mapToObj(i -> game.computeBaseScore(i / 100d))
                .collect(Collectors.toList());

        var sortedScores = scores.stream().sorted().collect(Collectors.toList());

        assertEquals(sortedScores, scores);
    }

    @Test
    void computeBaseScoreIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> game.computeBaseScore(-1d));
        assertThrows(IllegalArgumentException.class, () -> game.computeBaseScore(2d));
    }

    @Test
    void computeStreakScoreNoStreak() {
        joePlayer.setStreak(0);
        assertEquals(100, game.computeStreakScore(joePlayer, 100));
    }

    @Test
    void computeStreakScore1Streak() {
        joePlayer.setStreak(1);
        assertEquals(100, game.computeStreakScore(joePlayer, 100));
    }

    @Test
    void computeStreakScore2Streak() {
        joePlayer.setStreak(2);
        assertEquals(200, game.computeStreakScore(joePlayer, 100));
    }

    @Test
    void computeStreakScore3Streak() {
        joePlayer.setStreak(3);
        assertEquals(200, game.computeStreakScore(joePlayer, 100));
    }

    @Test
    void updateStreakWrong() {
        joePlayer.setStreak(0);

        game.updateStreak(joePlayer, false);
        assertEquals(0, joePlayer.getStreak());
    }

    @Test
    void updateStreakCorrect() {
        joePlayer.setStreak(0);

        game.updateStreak(joePlayer, true);
        assertEquals(1, joePlayer.getStreak());
    }

    @Test
    void updateStreakCorrectMultiple() {
        joePlayer.setStreak(0);

        game.updateStreak(joePlayer, true);
        game.updateStreak(joePlayer, true);
        game.updateStreak(joePlayer, true);
        game.updateStreak(joePlayer, true);
        assertEquals(4, joePlayer.getStreak());
    }

    @Test
    void updateStreakWrongReset() {
        joePlayer.setStreak(5);

        game.updateStreak(joePlayer, false);
        assertEquals(0, joePlayer.getStreak());
    }

    @Test
    void updateScoresCorrect() {
        var answerA = new Answer();
        answerA.setPlayer(joePlayer);
        answerA.setResponse(List.of(questionA.getAnswer().getCost()));

        var answerB = new Answer();
        answerB.setPlayer(susannePlayer);
        answerB.setResponse(List.of(questionA.getAnswer().getCost()));

        game.updateScores(questionA, List.of(answerA, answerB));
        assertEquals(100, joePlayer.getScore());
        assertEquals(1, joePlayer.getStreak());
        assertEquals(1, joePlayer.getPowerUpPoints());

        assertEquals(100, susannePlayer.getScore());
        assertEquals(1, susannePlayer.getStreak());
        assertEquals(1, susannePlayer.getPowerUpPoints());

    }

    @Test
    void updateScoresHalf() {
        var answerA = new Answer();
        answerA.setPlayer(joePlayer);
        answerA.setResponse(List.of(questionA.getAnswer().getCost()));

        var answerB = new Answer();
        answerB.setPlayer(susannePlayer);
        answerB.setResponse(List.of(300L));

        game.updateScores(questionA, List.of(answerA, answerB));
        assertEquals(100, joePlayer.getScore());
        assertEquals(1, joePlayer.getStreak());
        assertEquals(1, joePlayer.getPowerUpPoints());

        assertEquals(-10, susannePlayer.getScore());
        assertEquals(0, susannePlayer.getStreak());
        assertEquals(0, susannePlayer.getPowerUpPoints());
    }

    @Test
    void updateScoresWrong() {
        joePlayer.setStreak(12);

        var answerA = new Answer();
        answerA.setPlayer(joePlayer);
        answerA.setResponse(List.of(300L));

        var answerB = new Answer();
        answerB.setPlayer(susannePlayer);
        answerB.setResponse(List.of(300L));

        game.updateScores(questionB, List.of(answerA, answerB));
        assertEquals(-10, joePlayer.getScore());
        assertEquals(0, joePlayer.getStreak());
        assertEquals(0, joePlayer.getPowerUpPoints());

        assertEquals(-10, susannePlayer.getScore());
        assertEquals(0, susannePlayer.getStreak());
        assertEquals(0, susannePlayer.getPowerUpPoints());
    }

    @Test
    void updatePowerUpPointsCorrect() {
        joePlayer.setPowerUpPoints(0);

        game.updatePowerUpPoints(joePlayer, true);
        assertEquals(1, joePlayer.getPowerUpPoints());
    }

    @Test
    void updatePowerUpPointsWrong() {
        joePlayer.setPowerUpPoints(0);

        game.updatePowerUpPoints(joePlayer, false);
        assertEquals(0, joePlayer.getPowerUpPoints());
    }
}

package server.database.entities.game;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static server.utils.TestHelpers.getUUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.entities.game.GameStatus;
import commons.entities.game.NormalGameDTO;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.entities.User;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.database.entities.game.exceptions.LastPlayerRemovedException;
import server.database.entities.question.MCQuestion;

/**
 * Tests for NormalGame class.
 */
public class NormalGameTest {
    NormalGame game;
    NormalGameConfiguration config;
    User joe;
    User susanne;
    GamePlayer joePlayer;
    GamePlayer susannePlayer;
    MCQuestion questionA;
    MCQuestion questionB;

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

        // Create config
        config = new NormalGameConfiguration(17, 13, 2);

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
        game.setCurrentQuestion(1);
        assertEquals(Optional.of(questionB), game.getQuestion());
    }

    @Test
    void getQuestionOutOfBounds() {
        game.setCurrentQuestion(2);
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
}
package server.database.entities;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.entities.game.NormalGameDTO;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.entities.game.GamePlayer;
import server.database.entities.game.NormalGame;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.database.entities.question.MCQuestion;

/**
 * Tests for NormalGame class.
 */
public class NormalGameTest {
    NormalGame game;
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
        joe.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        joePlayer = new GamePlayer(joe);
        joePlayer.setId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
        joePlayer.setJoinDate(LocalDateTime.parse("2020-03-04T00:00:00"));

        susanne = new User("Susanne", "susanne@louisiane.com", "stinkymonkey");
        susanne.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        susannePlayer = new GamePlayer(susanne);
        susannePlayer.setId(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"));
        susannePlayer.setJoinDate(LocalDateTime.parse("2022-03-03T00:00:00"));

        // Create questions
        questionA = new MCQuestion();
        questionB = new MCQuestion();

        // Create the game
        game = new NormalGame();
        game.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        game.setConfiguration(new NormalGameConfiguration(17, 13, 2));
        game.addQuestions(Arrays.asList(questionA, questionB));
        game.add(joePlayer);
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
        // Exclude players, questions, and head as the created lobby must be empty
        NormalGame repl = new NormalGame(dto);
        assertThat(game)
                .usingRecursiveComparison()
                .ignoringFields("players", "questions", "head", "random")
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
    void size() {
        assertEquals(1, game.size());
    }

    @Test
    void isNotFull() {
        assertFalse(game.isFull());
    }

    @Test
    void isFull() {
        // Add a player and check that the lobby is now full
        game.add(susannePlayer);
        assertTrue(game.isFull());
    }
}

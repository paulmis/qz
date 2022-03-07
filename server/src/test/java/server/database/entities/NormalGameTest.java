package server.database.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.game.NormalGameDTO;
import commons.entities.utils.DTO;
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
    GamePlayer joe;
    MCQuestion questionA;
    MCQuestion questionB;

    @BeforeEach
    void init() {
        joe = new GamePlayer();
        questionA = new MCQuestion();
        questionB = new MCQuestion();

        game = new NormalGame();
        game.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        game.setConfiguration(new NormalGameConfiguration(17, 13, 6));
        game.addQuestions(Arrays.asList(questionA, questionB));
    }

    @Test
    void convert() throws JsonProcessingException {
        // Create a ObjectMapper
        ObjectMapper om = new ObjectMapper().registerModule(new Jdk8Module());

        // Clear players and quetions
        game.setPlayers(new HashSet<>());
        game.setQuestions(new ArrayList<>());

        // Serialize and deserialize
        String string = om.writeValueAsString(game.getDTO());
        DTO dto = om.readValue(string, NormalGameDTO.class);

        // Initiate a copy of the game
        NormalGame repl = new NormalGame((NormalGameDTO) dto);
        assertEquals(game, repl);
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
        game.add(joe);
        assertEquals(1, game.size());
    }
}

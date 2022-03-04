package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.AnswerDTO;
import commons.entities.UserDTO;
import commons.entities.game.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import server.database.entities.game.Game;
import server.database.entities.question.Activity;
import server.database.entities.question.Question;
import server.database.repositories.game.GameRepository;
import server.database.repositories.question.QuestionRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWebMvc
class AnswerControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private GameRepository gameRepository;

    // ToDo: remove this class when a subclass of game is implemented
    private class MockGame extends Game {
        @Override
        public Optional<Question> getNextQuestion() {
            return Optional.empty();
        }
    }

    private UUID getUUID(int id) {
        return UUID.fromString("00000000-0000-0000-0000-00000000000" + (id % 10));
    }

    private Game mockLobby;

    @Autowired
    public AnswerControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    }

    @BeforeEach
    private void init() {
        // Mock a working gameId
        mockLobby = new AnswerControllerTest.MockGame();
        mockLobby.setId(getUUID(0));
        mockLobby.setStatus(GameStatus.CREATED);
        when(gameRepository.findById(mockLobby.getId())).thenReturn(Optional.of(mockLobby));
    }

    // ToDo: test failing as of now
    @Test
    public void userAnswerOkTest() throws Exception {
        // Request
        AnswerDTO userAnswer = new AnswerDTO();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/api/game/" + mockLobby.getId() + "/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAnswer)))
                        .andExpect(status().isOk());
    }

    // ToDo: test failing as of now
    @Test
    public void userAnswerNotFoundTest() throws Exception {
        // Request
        AnswerDTO userAnswer = new AnswerDTO();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/api/game/" + getUUID(1) + "/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAnswer)))
                .andExpect(status().isNotFound());
    }

}
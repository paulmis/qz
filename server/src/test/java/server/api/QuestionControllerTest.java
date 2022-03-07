package server.api;

import static org.hamcrest.Matchers.equalToObject;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.AnswerDTO;
import commons.entities.UserDTO;
import commons.entities.game.GameStatus;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import server.database.entities.User;
import server.database.entities.game.Game;
import server.database.entities.question.Question;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameRepository;
import server.database.repositories.question.QuestionRepository;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWebMvc
class QuestionControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private GameRepository gameRepository;

    @MockBean
    private QuestionRepository questionRepository;

    @MockBean
    private UserRepository userRepository;

    // ToDo: remove this class when a subclass of game is implemented
    private class MockGame extends Game {
        @Override
        public Optional<Question> getNextQuestion() {
            return Optional.empty();
        }
    }

    private class MockQuestion extends Question {
        @Override
        public List<Double> checkAnswer(List<AnswerDTO> userAnswers) throws IllegalArgumentException {
            return null;
        }
    }

    private UUID getUUID(int id) {
        return UUID.fromString("00000000-0000-0000-0000-00000000000" + (id % 10));
    }

    private Game mockGame;
    private Question mockQuestion;
    User joe;
    UserDTO joeDTO;

    @Autowired
    public QuestionControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    }

    @BeforeEach
    private void init() {
        mockGame = new QuestionControllerTest.MockGame();
        mockGame.setId(getUUID(0));
        mockGame.setStatus(GameStatus.ONGOING);
        when(gameRepository.existsById(mockGame.getId())).thenReturn(true);

        // Setup mock question
        mockQuestion = new MockQuestion();
        mockQuestion.setId(getUUID(0));
        mockQuestion.setText("Test Question");
        when(questionRepository.findById(mockQuestion.getId())).thenReturn(Optional.of(mockQuestion));

        //Set up random test user
        joe = new User("joe", "joe@doe.com", "stinkywinky");
        joe.setId(getUUID(0));
        joeDTO = joe.getDTO();

        // Set the context user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        joe.getEmail(),
                        joe.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    @Test
    public void questionFoundTest() throws Exception {
        // Request question object -> expect a ok status and mock question object
        this.mockMvc
                .perform(get("/api/game/" + mockGame.getId() + "/question/" + mockQuestion.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(equalToObject(
                        objectMapper.writeValueAsString(mockQuestion.getDTO())
                )));
    }

    @Test
    public void questionNotFoundTest() throws Exception {
        // Request question object -> expect a not found status
        this.mockMvc
                .perform(get("/api/game/" + mockGame.getId() + "/question/" + getUUID(1)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void gameNotFoundTest() throws Exception {
        // Request question object -> expect a not found status
        this.mockMvc
                .perform(get("/api/game/" + getUUID(1) + "/question/" + mockQuestion.getId()))
                .andExpect(status().isNotFound());
    }
}

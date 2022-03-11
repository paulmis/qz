package server.api;

import static org.hamcrest.Matchers.equalToObject;
import static org.mockito.Mockito.when;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import server.database.entities.User;
import server.database.entities.game.Game;
import server.database.entities.game.GamePlayer;
import server.database.entities.game.NormalGame;
import server.database.entities.question.Activity;
import server.database.entities.question.MCQuestion;
import server.database.entities.question.Question;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GamePlayerRepository;
import server.database.repositories.game.GameRepository;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWebMvc
class AnswerControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private GameRepository gameRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GamePlayerRepository gamePlayerRepository;

    private UUID getUUID(int id) {
        return UUID.fromString("00000000-0000-0000-0000-00000000000" + (id % 10));
    }

    private static Activity getActivity(int id) {
        Activity a = new Activity();
        a.setDescription("Activity" + (id + 1));
        a.setCost(2 + id * 4);
        return a;
    }

    private Game mockLobby;
    private Question mockQuestion;
    private User joe;
    private UserDTO joeDTO;
    private GamePlayer joePlayer;

    @Autowired
    public AnswerControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    }

    @BeforeEach
    private void init() {
        // Setup mock question
        mockQuestion = new MCQuestion();
        mockQuestion.setActivities(List.of(
                getActivity(1),
                getActivity(2),
                getActivity(3),
                getActivity(4)));
        ((MCQuestion) mockQuestion).setAnswer(mockQuestion.getActivities().get(1));

        // Set up a mock game
        mockLobby = new NormalGame();
        mockLobby.setId(getUUID(0));
        mockLobby.setStatus(GameStatus.ONGOING);
        mockLobby.setQuestions(List.of(mockQuestion));
        mockLobby.setCurrentQuestion(0);
        when(gameRepository.existsById(mockLobby.getId())).thenReturn(true);
        when(gameRepository.findById(mockLobby.getId())).thenReturn(Optional.of(mockLobby));

        // Set up mock user
        joe = new User("joe", "joe@doe.com", "stinkywinky");
        joe.setId(getUUID(0));
        joeDTO = joe.getDTO();

        // Set up a mock gamePlayer
        joePlayer = new GamePlayer();
        joePlayer.setUser(joe);
        joePlayer.setGame(mockLobby);
        when(gamePlayerRepository.existsByUserIdAndGameId(joe.getId(), mockLobby.getId())).thenReturn(true);

        // Set the context user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        joe.getEmail(),
                        joe.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
        when(userRepository.findByEmail(joe.getEmail())).thenReturn(Optional.of(joe));
    }

    @Test
    public void userAnswerOkTest() throws Exception {
        // Request
        AnswerDTO userAnswer = new AnswerDTO();
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/api/game/" + mockLobby.getId() + "/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAnswer)))
                .andExpect(status().isOk());
    }

    @Test
    public void userAnswerNotFoundTest() throws Exception {
        // Request
        AnswerDTO userAnswer = new AnswerDTO();
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/api/game/" + getUUID(1) + "/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAnswer)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getCorrectAnswerTest() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/game/" + mockLobby.getId() + "/answer"))
                .andExpect(content().string(equalToObject(
                        objectMapper.writeValueAsString(mockQuestion.getRightAnswer().getDTO()))));
    }

    @Test
    public void getCorrectAnswerDifferentIdxTest() throws Exception {
        // Setup additional mock question
        Question secondQuestion = new MCQuestion();
        secondQuestion.setActivities(List.of(
                getActivity(10),
                getActivity(20),
                getActivity(30),
                getActivity(40)));
        ((MCQuestion) secondQuestion).setAnswer(secondQuestion.getActivities().get(0));
        mockLobby.setQuestions(List.of(mockQuestion, secondQuestion));
        mockLobby.setCurrentQuestion(0);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/game/" + mockLobby.getId() + "/answer?idx=1"))
                .andExpect(content().string(equalToObject(
                        objectMapper.writeValueAsString(secondQuestion.getRightAnswer().getDTO()))));
    }

    @Test
    public void getCorrectAnswerOutOfIndexTest() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/game/" + mockLobby.getId() + "/answer?idx=1"))
                .andExpect(content().string(equalToObject(
                        objectMapper.writeValueAsString(mockQuestion.getRightAnswer().getDTO()))));
    }

    @Test
    public void getCorrectAnswerNegativeIndexTest() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/game/" + mockLobby.getId() + "/answer?idx=-1"))
                .andExpect(content().string(equalToObject(
                        objectMapper.writeValueAsString(mockQuestion.getRightAnswer().getDTO()))));
    }

    @Test
    public void getCorrectAnswerNoQuestionTest() throws Exception {
        mockLobby.setCurrentQuestion(1);
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/game/" + mockLobby.getId() + "/answer"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getCorrectAnswerWrongGameTest() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/game/" + getUUID(1) + "/answer"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getCorrectAnswerUserNotAllowedTest() throws Exception {
        // Set up wrong user
        User susan = new User("susan", "susan@anas.com", "stinkypinky");
        susan.setId(getUUID(1));

        // Set the context user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        susan.getEmail(),
                        susan.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
        when(userRepository.findByEmail(susan.getEmail())).thenReturn(Optional.of(susan));

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/game/" + mockLobby.getId() + "/answer"))
                .andExpect(status().isForbidden());
    }
}

package server.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.AnswerDTO;
import commons.entities.UserDTO;
import commons.entities.game.GameStatus;
import java.util.Collections;
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
import server.database.entities.game.NormalGame;
import server.database.repositories.UserRepository;
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

    private UUID getUUID(int id) {
        return UUID.fromString("00000000-0000-0000-0000-00000000000" + (id % 10));
    }

    private Game mockLobby;
    User joe;
    UserDTO joeDTO;

    @Autowired
    public AnswerControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    }

    @BeforeEach
    private void init() {
        // Mock a working gameId
        mockLobby = new NormalGame();
        mockLobby.setId(getUUID(0));
        mockLobby.setStatus(GameStatus.CREATED);
        when(gameRepository.existsById(mockLobby.getId())).thenReturn(true);
        
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
    public void userAnswerOkTest() throws Exception {
        // Request
        AnswerDTO userAnswer = new AnswerDTO();
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/api/game/" + mockLobby.getId() + "/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAnswer)))
                        .andExpect(status().isOk());
    }

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
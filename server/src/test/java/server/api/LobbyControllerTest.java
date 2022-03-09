package server.api;

import static org.hamcrest.Matchers.equalToObject;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static server.TestHelpers.getUUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.UserDTO;
import commons.entities.game.GamePlayerDTO;
import commons.entities.game.GameStatus;
import java.util.ArrayList;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import server.database.entities.User;
import server.database.entities.game.Game;
import server.database.entities.game.GamePlayer;
import server.database.entities.question.Question;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameRepository;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWebMvc
class LobbyControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private GameRepository gameRepository;

    @MockBean
    private UserRepository userRepository;

    // ToDo: remove this class when a subclass of game is implemented
    private class MockGame extends Game {
        @Override
        public Optional<Question> getNextQuestion() {
            return Optional.empty();
        }
    }

    private Game mockLobby;
    private User john;

    @Autowired
    public LobbyControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    }

    @BeforeEach
    private void init() {
        // Mock a working gameId
        mockLobby = new MockGame();
        mockLobby.setId(getUUID(0));
        mockLobby.setStatus(GameStatus.CREATED);
        when(gameRepository.findById(mockLobby.getId())).thenReturn(Optional.of(mockLobby));

        // Mock a working userId
        john = new User();
        john.setId(getUUID(0));
        when(userRepository.findById(john.getId())).thenReturn(Optional.of(john));

        // Set the context user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        john.getEmail(),
                        john.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    @Test
    public void getAvailableLobbiesTest() throws Exception {
        // Mock a list of lobbies
        Game mockLobby2 = new MockGame();
        mockLobby2.setId(getUUID(1));
        mockLobby2.setStatus(GameStatus.CREATED);
        when(gameRepository.findAllByStatus(GameStatus.CREATED))
                .thenReturn(new ArrayList<>(List.of(mockLobby, mockLobby2)));

        // Request
        this.mockMvc.perform(get("/api/lobby/available"))
                .andExpect(status().isOk())
                .andExpect(content().string(equalToObject(
                        objectMapper.writeValueAsString(List.of(mockLobby.getDTO(), mockLobby2.getDTO()))
                )));
    }

    @Test
    public void lobbyInfoTest() throws Exception {
        // Request
        this.mockMvc.perform(get("/api/lobby/" + mockLobby.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(equalToObject(
                        objectMapper.writeValueAsString(mockLobby.getDTO())
                )));
    }

    @Test
    public void lobbyNotFoundInfoTest() throws Exception {
        // Request
        this.mockMvc
                .perform(get("/api/lobby/" + getUUID(1)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void joinOkTest() throws Exception {
        // Request
        UserDTO player = new UserDTO();
        this.mockMvc
                .perform(put("/api/lobby/" + mockLobby.getId() + "/join/" + john.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player)))
                .andExpect(status().isOk());
    }

    @Test
    public void lobbyNotFoundJoinTest() throws Exception {
        // Request
        GamePlayerDTO player = new GamePlayerDTO();
        this.mockMvc
                .perform(put("/api/lobby/" + getUUID(1) + "/join/" + john.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void userNotFoundJoinTest() throws Exception {
        // Request
        GamePlayerDTO player = new GamePlayerDTO();
        this.mockMvc
                .perform(put("/api/lobby/" + mockLobby.getId() + "/join/" + getUUID(1))
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(player)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void joinAlreadyPresentTest() throws Exception {
        // Mock a joined player
        GamePlayerDTO player = new GamePlayerDTO();
        GamePlayer playerJohn = new GamePlayer(player);
        playerJohn.setUser(john);
        mockLobby.add(playerJohn);

        // Request
        this.mockMvc
                .perform(put("/api/lobby/" + mockLobby.getId() + "/join/" + john.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player)))
                .andExpect(status().isConflict());
    }

    @Test
    public void joinStartedGameTest() throws Exception {
        // Mock a started game
        mockLobby.setStatus(GameStatus.ONGOING);

        // Request
        UserDTO player = new UserDTO();
        this.mockMvc
                .perform(put("/api/lobby/" + mockLobby.getId() + "/join/" + john.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player)))
                .andExpect(status().isConflict());
    }
}
package server.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static server.utils.TestHelpers.getUUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.auth.UserDTO;
import commons.entities.game.GameStatus;
import commons.entities.messages.SSEMessage;
import commons.entities.utils.Views;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import server.database.entities.User;
import server.database.entities.game.Game;
import server.database.entities.game.GamePlayer;
import server.database.entities.game.NormalGame;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameRepository;
import server.services.SSEManager;

/**
 * Tests for UserController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@EnableWebMvc
public class UserControllerTests {
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GameRepository gameRepository;

    @MockBean
    private SSEManager sseManager;

    User joe;
    UserDTO joeDTO;
    private Game game;

    /**
     * Initializes the test suite.
     *
     * @param mockMvc the autoconfigured mock mvc
     */
    @Autowired
    public UserControllerTests(MockMvc mockMvc) {
        this.mvc = mockMvc;
        this.objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    }

    @BeforeEach
    private void init() {
        joe = new User("joe", "joe@doe.com", "stinkywinky");
        joe.setId(getUUID(0));
        joeDTO = joe.getDTO();

        // Set the context user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                    joe.getEmail(),
                    joe.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

        // Create a lobby
        game = new NormalGame();
        game.setId(getUUID(3));
        game.setStatus(GameStatus.ONGOING);
        var gameConfiguration = new NormalGameConfiguration(10, Duration.ofSeconds(10), 2, 2, 2f, 100, 0, 75);
        game.setConfiguration(gameConfiguration);
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

        // Add players
        var joePlayer = new GamePlayer(joe);
        game.add(joePlayer);
        when(gameRepository.getPlayersLobbyOrGame(joe.getId())).thenReturn(Optional.of(game));
    }

    @Test
    void getOk() throws Exception {
        // Mock the repository
        when(userRepository.findByEmailIgnoreCase(joe.getEmail())).thenReturn(Optional.of(joe));

        // Perform the request
        MvcResult res = this.mvc
                .perform(
                        get("/api/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(joeDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        objectMapper.writerWithView(Views.Private.class).writeValueAsString(joe.getDTO())))
                .andReturn();
    }

    @Test
    void getNotFound() throws Exception {
        // Mock the repository
        when(userRepository.findByEmailIgnoreCase(joe.getEmail())).thenReturn(Optional.empty());

        // Perform the request
        MvcResult res = this.mvc
                .perform(
                        get("/api/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(joeDTO)))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void changeUsernameOk() throws Exception {
        // Mock the repository
        when(userRepository.findByEmailIgnoreCase(joe.getEmail())).thenReturn(Optional.of(joe));
        when(userRepository.existsByUsername("joe")).thenReturn(true);

        this.mvc.perform(post("/api/user/username")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("George Bush"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void changeUsernameSame() throws Exception {
        // Mock the repository
        when(userRepository.findByEmailIgnoreCase(joe.getEmail())).thenReturn(Optional.of(joe));
        when(userRepository.existsByUsername("joe")).thenReturn(true);

        this.mvc.perform(
                post("/api/user/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("joe"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void changeUsernameUsed() throws Exception {
        // Mock the repository
        when(userRepository.findByEmailIgnoreCase(joe.getEmail())).thenReturn(Optional.of(joe));
        when(userRepository.existsByUsername("joe")).thenReturn(true);
        when(userRepository.existsByUsername("Donald Trump")).thenReturn(true);

        this.mvc.perform(
                post("/api/user/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("Donald Trump"))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    void changeUsernameSSE() throws Exception {
        // Mock the repository
        when(userRepository.findByEmailIgnoreCase(joe.getEmail())).thenReturn(Optional.of(joe));
        when(userRepository.existsByUsername("joe")).thenReturn(true);

        this.mvc.perform(
                post("/api/user/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("George Bush"))
                .andExpect(status().isOk())
                .andReturn();

        verify(sseManager, times(1)).send(any(Iterable.class), any(SSEMessage.class));
        verifyNoMoreInteractions(sseManager);
    }

    @Test
    void changeUsernameNoSSE() throws Exception {
        // Mock the repository
        when(userRepository.findByEmailIgnoreCase(joe.getEmail())).thenReturn(Optional.of(joe));
        when(userRepository.existsByUsername("joe")).thenReturn(true);
        when(userRepository.existsByUsername("Donald Trump")).thenReturn(true);

        this.mvc.perform(
                post("/api/user/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("Donald Trump"))
                .andExpect(status().is4xxClientError())
                .andReturn();

        verifyNoMoreInteractions(sseManager);
    }

    @Test
    void changeUsernameShort() throws Exception {
        // Mock the repository
        when(userRepository.findByEmailIgnoreCase(joe.getEmail())).thenReturn(Optional.of(joe));
        when(userRepository.existsByUsername("joe")).thenReturn(true);

        this.mvc.perform(
                post("/api/user/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("G"))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    void changeUsernameLong() throws Exception {
        // Mock the repository
        when(userRepository.findByEmailIgnoreCase(joe.getEmail())).thenReturn(Optional.of(joe));
        when(userRepository.existsByUsername("joe")).thenReturn(true);

        this.mvc.perform(
                post("/api/user/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("Gsssssssssssssssssssssssssssssssssssssssssssssssss"))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }
}

package server.api;

import static org.hamcrest.Matchers.equalToObject;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static server.utils.TestHelpers.getUUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.entities.game.GameStatus;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
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
import server.database.entities.game.NormalGame;
import server.database.entities.game.configuration.GameConfiguration;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.database.entities.game.configuration.SurvivalGameConfiguration;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameConfigurationRepository;
import server.database.repositories.game.GamePlayerRepository;
import server.database.repositories.game.GameRepository;
import server.services.GameService;
import server.services.LobbyService;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWebMvc
class LobbyControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private GameRepository gameRepository;

    @MockBean
    private GameService gameService;

    @MockBean
    private GamePlayerRepository gamePlayerRepository;

    @MockBean
    private GameConfigurationRepository gameConfigurationRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private LobbyService lobbyService;

    private NormalGame mockLobby;
    private GameConfiguration mockLobbyConfiguration;
    private NormalGameConfiguration normalGameConfiguration;
    private User john;
    private User susanne;
    private User sally;
    private GamePlayer johnPlayer;
    private GamePlayer susannePlayer;

    @Autowired
    public LobbyControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper().registerModules(new Jdk8Module(), new JavaTimeModule());
    }

    @BeforeEach
    private void init() {
        // Mock users
        john = new User("John", "john@upon.com", "stinkydonkey");
        john.setId(getUUID(0));
        when(userRepository.findById(john.getId())).thenReturn(Optional.of(john));
        when(userRepository.findByEmailIgnoreCase(john.getEmail())).thenReturn(Optional.of(john));

        susanne = new User("Susanne", "susanne@louisiane.com", "stinkymonkey");
        susanne.setId(getUUID(1));
        when(userRepository.findById(susanne.getId())).thenReturn(Optional.of(susanne));
        when(userRepository.findByEmailIgnoreCase(susanne.getEmail())).thenReturn(Optional.of(susanne));

        sally = new User("Sally", "sally@wally.com", "stinkybinky");
        sally.setId(getUUID(2));
        when(userRepository.findById(sally.getId())).thenReturn(Optional.of(sally));
        when(userRepository.findByEmailIgnoreCase(sally.getEmail())).thenReturn(Optional.of(sally));

        // Create a lobby
        mockLobby = new NormalGame();
        mockLobby.setId(getUUID(3));
        mockLobby.setStatus(GameStatus.CREATED);
        mockLobbyConfiguration = new NormalGameConfiguration(10, Duration.ofSeconds(10), 2, 2, 2f, 100, 0, 75);
        mockLobby.setConfiguration(mockLobbyConfiguration);
        normalGameConfiguration = new NormalGameConfiguration(4, Duration.ofSeconds(8), 6, 2, 2f, 100, 0, 75);

        // Add players
        johnPlayer = new GamePlayer(john);
        mockLobby.add(johnPlayer);
        when(gameRepository.findByPlayers_User_IdEqualsAndStatus(john.getId(), GameStatus.CREATED))
                .thenReturn(Optional.of(mockLobby));
        susannePlayer = new GamePlayer(susanne);
        mockLobby.add(susannePlayer);
        when(gameRepository.findByPlayers_User_IdEqualsAndStatus(susanne.getId(), GameStatus.CREATED))
                .thenReturn(Optional.of(mockLobby));

        // Mock the lobby
        when(gameRepository.findById(mockLobby.getId())).thenReturn(Optional.of(mockLobby));
        when(gameRepository.save(any(NormalGame.class))).thenReturn((NormalGame) mockLobby);

        // Set the context user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        john.getEmail(),
                        john.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    @Test
    public void getAvailable() throws Exception {
        // Mock a list of lobbies
        NormalGame otherLobby = new NormalGame();
        otherLobby.setId(getUUID(1));
        otherLobby.setStatus(GameStatus.CREATED);
        otherLobby.setConfiguration(new NormalGameConfiguration());
        when(gameRepository.findAllByStatus(GameStatus.CREATED))
                .thenReturn(new ArrayList<>(List.of(mockLobby, otherLobby)));

        // Request
        this.mockMvc.perform(get("/api/lobby/available"))
                .andExpect(status().isOk());
    }

    @Test
    public void getOk() throws Exception {
        // Request
        this.mockMvc.perform(get("/api/lobby/" + mockLobby.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(equalToObject(
                        objectMapper.writeValueAsString(mockLobby.getDTO())
                )));
    }

    @Test
    public void getNotFound() throws Exception {
        // Request
        this.mockMvc
                .perform(get("/api/lobby/" + getUUID(1)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void configGetConfigTest() throws Exception {
        this.mockMvc
                .perform(get("/api/lobby/" + mockLobby.getId() + "/config"))
                .andExpect(content().string(equalToObject(
                        objectMapper.writeValueAsString(mockLobby.getConfiguration().getDTO())
                )));
    }

    @Test
    public void configGetLobbyNotFoundTest() throws Exception {
        this.mockMvc
                .perform(get("/api/lobby/" + getUUID(1) + "/config"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void joinOk() throws Exception {
        // Modify the capacity to allow the player to join
        mockLobbyConfiguration.setCapacity(3);

        // Set the context user to a user that isn't in the game
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        sally.getEmail(),
                        sally.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

        // Request
        this.mockMvc
                .perform(put("/api/lobby/" + mockLobby.getId() + "/join"))
                .andExpect(status().isOk());
    }

    @Test
    public void lobbyNotFoundJoin() throws Exception {
        this.mockMvc
                .perform(put("/api/lobby/" + getUUID(1) + "/join"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void joinAlreadyPresent() throws Exception {
        this.mockMvc
                .perform(put("/api/lobby/" + mockLobby.getId() + "/join"))
                .andExpect(status().isConflict());
    }

    @Test
    public void joinStartedGame() throws Exception {
        // Mock a started game
        mockLobby.setStatus(GameStatus.ONGOING);

        // Request
        this.mockMvc
                .perform(put("/api/lobby/" + mockLobby.getId() + "/join"))
                .andExpect(status().isConflict());
    }

    @Test
    public void configPostLobbyConfigurationUpdateTest() throws Exception {
        mockLobby.setStatus(GameStatus.CREATED);
        mockLobby.setHost(johnPlayer);

        // Request
        this.mockMvc
                .perform(post("/api/lobby/" + mockLobby.getId() + "/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(normalGameConfiguration.getDTO())))
                .andExpect(status().isOk());
    }

    @Test
    public void configPostLobbyConfigurationUpdateNotAcceptableTest() throws Exception {
        mockLobby.setStatus(GameStatus.CREATED);
        mockLobby.setHost(johnPlayer);
        SurvivalGameConfiguration survivalGameConfiguration = new SurvivalGameConfiguration(1.25f);

        // Request
        this.mockMvc
                .perform(post("/api/lobby/" + mockLobby.getId() + "/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(survivalGameConfiguration.getDTO())))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void configPostLobbyConfigurationUpdatedTest() throws Exception {
        mockLobby.setStatus(GameStatus.CREATED);
        mockLobby.setHost(johnPlayer);

        // Send the new configuration
        this.mockMvc
                .perform(post("/api/lobby/" + mockLobby.getId() + "/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(normalGameConfiguration.getDTO())))
                .andExpect(status().isOk());

        // Check if configuration has indeed updated
        this.mockMvc
                .perform(get("/api/lobby/" + mockLobby.getId() + "/config"))
                .andExpect(status().isOk())
                .andExpect(content().string(equalToObject(
                        objectMapper.writeValueAsString(normalGameConfiguration.getDTO())
                )));
    }

    @Test
    public void configPostLobbyNotFoundTest() throws Exception {
        mockLobby.setStatus(GameStatus.CREATED);
        mockLobby.setHost(johnPlayer);

        // Request
        this.mockMvc
                .perform(post("/api/lobby/" + getUUID(5) + "/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(normalGameConfiguration.getDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void configPostUserNotFoundTest() throws Exception {
        when(userRepository.findByEmailIgnoreCase(john.getEmail())).thenReturn(Optional.empty());
        mockLobby.setStatus(GameStatus.CREATED);

        // Request
        this.mockMvc
                .perform(post("/api/lobby/" + getUUID(5) + "/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(normalGameConfiguration.getDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void configPostLobbyNotCreatedTest() throws Exception {
        mockLobby.setStatus(GameStatus.ONGOING);

        // Request
        this.mockMvc
                .perform(post("/api/lobby/" + mockLobby.getId() + "/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(normalGameConfiguration.getDTO())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void configPostUserNotHostTest() throws Exception {
        mockLobby.setStatus(GameStatus.CREATED);
        mockLobby.setHost(susannePlayer);

        // Request
        this.mockMvc
                .perform(post("/api/lobby/" + mockLobby.getId() + "/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(normalGameConfiguration.getDTO())))
                .andExpect(status().isForbidden());
    }

    @Test
    void createCreated() throws Exception {
        // Mock the repositories
        when(gamePlayerRepository.existsByUserIdAndGameStatusNot(john.getId(), GameStatus.FINISHED))
                .thenReturn(false);
        when(gameConfigurationRepository.save(mockLobbyConfiguration))
                .thenReturn(mockLobbyConfiguration);

        // Request
        this.mockMvc
                .perform(post("/api/lobby")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mockLobby.getDTO())))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(mockLobby.getDTO())));
    }

    @Test
    void createUserNotFound() throws Exception {
        // Override the user repository response
        when(userRepository.findByEmailIgnoreCase(john.getEmail())).thenReturn(Optional.empty());

        // Request
        this.mockMvc
                .perform(post("/api/lobby")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockLobby.getDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUserAlreadyInGame() throws Exception {
        // Mock the repositories
        when(gamePlayerRepository.existsByUserIdAndGameStatusNot(john.getId(), GameStatus.FINISHED))
                .thenReturn(true);

        // Request
        this.mockMvc
                .perform(post("/api/lobby")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockLobby.getDTO())))
                .andExpect(status().isConflict());
    }

    @Test
    void createBadRequest() throws Exception {
        // Mock the repositories
        when(gamePlayerRepository.existsByUserIdAndGameStatusNot(john.getId(), GameStatus.FINISHED))
                .thenReturn(false);
        when(gameConfigurationRepository.save(mockLobbyConfiguration))
                .thenThrow(ConstraintViolationException.class);

        // Request
        this.mockMvc
                .perform(post("/api/lobby")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockLobby.getDTO())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void startOk() throws Exception {
        // Request
        this.mockMvc
                .perform(put("/api/lobby/" + mockLobby.getId() + "/start"))
                .andExpect(status().isOk());

        // Verify that the game has been started
        verify(gameService).startGame(mockLobby);
        verifyNoMoreInteractions(gameService);
    }

    @Test
    void startNotFound() throws Exception {
        // Request
        this.mockMvc
                .perform(put("/api/lobby/" + getUUID(1) + "/start"))
                .andExpect(status().isNotFound());
    }

    @Test
    void startHeadMismatchForbidden() throws Exception {
        // Change the head
        mockLobby.setHost(susannePlayer);

        // Request
        this.mockMvc
                .perform(put("/api/lobby/" + mockLobby.getId() + "/start"))
                .andExpect(status().isForbidden());
    }

    @Test
    void startServiceException() throws Exception {
        // Mock the game service to throw an exception (e.g. due to violated constraints)
        doThrow(IllegalStateException.class).when(gameService).startGame(mockLobby);

        // Request
        this.mockMvc
                .perform(put("/api/lobby/" + mockLobby.getId() + "/start"))
                .andExpect(status().isConflict());
    }

    @Test
    void leaveOk() throws Exception {
        // Mock the service
        when(lobbyService.removePlayer(mockLobby, john)).thenReturn(true);

        // Request
        this.mockMvc
                .perform(delete("/api/lobby/leave"))
                .andExpect(status().isOk());
    }

    @Test
    void leaveNotFound() throws Exception {
        // Mock the service
        when(lobbyService.removePlayer(mockLobby, john)).thenReturn(false);

        // Set the context user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        sally.getEmail(),
                        sally.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

        // Request
        this.mockMvc
                .perform(delete("/api/lobby/leave"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOk() throws Exception {
        // Mock the service
        when(lobbyService.deleteLobby(mockLobby, john)).thenReturn(true);

        // Request
        this.mockMvc
                .perform(delete("/api/lobby/delete"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteNotHost() throws Exception {
        // Set the context user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        susanne.getEmail(),
                        susanne.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

        // Mock the service
        when(lobbyService.deleteLobby(mockLobby, susanne)).thenReturn(false);

        // Request
        this.mockMvc
                .perform(delete("/api/lobby/delete"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteNoUser() throws Exception {
        // Set non-existent user
        User bobby = new User("Bobby", "bobby@solo.com", "stinkywhiskey");
        bobby.setId(getUUID(3));
        when(userRepository.findById(bobby.getId())).thenReturn(Optional.of(bobby));
        when(userRepository.findByEmailIgnoreCase(bobby.getEmail())).thenReturn(Optional.empty());

        // Set the context user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        bobby.getEmail(),
                        bobby.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
        when(gameRepository.findByPlayers_User_IdEqualsAndStatus(bobby.getId(), GameStatus.CREATED))
                .thenReturn(Optional.of(mockLobby));

        // Request
        this.mockMvc
                .perform(delete("/api/lobby/delete"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNoGame() throws Exception {
        // Set the context user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        sally.getEmail(),
                        sally.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
        when(gameRepository.findByPlayers_User_IdEqualsAndStatus(sally.getId(), GameStatus.CREATED))
                .thenReturn(Optional.empty());

        // Request
        this.mockMvc
                .perform(delete("/api/lobby/delete"))
                .andExpect(status().isNotFound());
    }
}
package server.api;

import static org.hamcrest.Matchers.equalToObject;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.game.GameStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Null;

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
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameConfigurationRepository;
import server.database.repositories.game.GamePlayerRepository;
import server.database.repositories.game.GameRepository;
import server.services.GameService;

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

    private UUID getUUID(int id) {
        return UUID.fromString("00000000-0000-0000-0000-00000000000" + (id % 10));
    }

    private Game mockLobby;
    private GameConfiguration mockLobbyConfiguration;
    private User john;
    private User susanne;
    private User sally;
    private GamePlayer johnPlayer;
    private GamePlayer susannePlayer;

    @Autowired
    public LobbyControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    }

    @BeforeEach
    private void init() {
        // Mock users
        john = new User("John", "john@upon.com", "stinkydonkey");
        john.setId(getUUID(0));
        when(userRepository.findById(john.getId())).thenReturn(Optional.of(john));
        when(userRepository.findByEmail(john.getEmail())).thenReturn(Optional.of(john));

        susanne = new User("Susanne", "susanne@louisiane.com", "stinkymonkey");
        susanne.setId(getUUID(1));
        when(userRepository.findById(susanne.getId())).thenReturn(Optional.of(susanne));
        when(userRepository.findByEmail(susanne.getEmail())).thenReturn(Optional.of(susanne));

        sally = new User("Sally", "sally@wally.com", "stinkybinky");
        sally.setId(getUUID(2));
        when(userRepository.findById(sally.getId())).thenReturn(Optional.of(sally));
        when(userRepository.findByEmail(sally.getEmail())).thenReturn(Optional.of(sally));

        // Create a lobby
        mockLobby = new NormalGame();
        mockLobby.setId(getUUID(3));
        mockLobby.setStatus(GameStatus.CREATED);
        mockLobbyConfiguration = new NormalGameConfiguration();
        mockLobby.setConfiguration(mockLobbyConfiguration);

        // Add players
        johnPlayer = new GamePlayer(john);
        susannePlayer = new GamePlayer(susanne);
        mockLobby.add(johnPlayer);
        mockLobby.add(susannePlayer);

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
    public void getAvailableLobbiesTest() throws Exception {
        // Mock a list of lobbies
        Game mockLobby2 = new NormalGame();
        mockLobby2.setId(getUUID(1));
        mockLobby2.setStatus(GameStatus.CREATED);
        mockLobby2.setConfiguration(new NormalGameConfiguration());
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
    public void configGetConfigTest() throws Exception {
        this.mockMvc
                .perform(get("/api/lobby/" + mockLobby.getId() + "/config"))
                .andExpect(content().string(equalToObject(
                        objectMapper.writeValueAsString(mockLobby.getDTO().getConfiguration())
                )));
    }

    @Test
    public void configGetLobbyNotFoundTest() throws Exception {
        this.mockMvc
                .perform(get("/api/lobby/" + getUUID(1) + "/config"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void configGetLobbyNotCreatedTest() throws Exception {
        //Check if the game has started or finished
        mockLobby.setStatus(GameStatus.FINISHED);
        this.mockMvc
                .perform(get("/api/lobby/" + mockLobby.getId() + "/config"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void joinOkTest() throws Exception {
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
    public void lobbyNotFoundJoinTest() throws Exception {
        this.mockMvc
                .perform(put("/api/lobby/" + getUUID(1) + "/join"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void joinAlreadyPresentTest() throws Exception {
        this.mockMvc
                .perform(put("/api/lobby/" + mockLobby.getId() + "/join"))
                .andExpect(status().isConflict());
    }

    @Test
    public void joinStartedGameTest() throws Exception {
        // Mock a started game
        mockLobby.setStatus(GameStatus.ONGOING);

        // Request
        this.mockMvc
                .perform(put("/api/lobby/" + mockLobby.getId() + "/join"))
                .andExpect(status().isConflict());
    }

    //ToDo: Test if lobby configuration is updated in lobbyConfiguration.
//    @Test
//    public void configPostLobbyConfigurationUpdatedTest() throws Exception {
//        // Mock the repositories
//        mockLobby.setStatus(GameStatus.CREATED);
//        mockLobby.setHost(johnPlayer);
//
//        // Request
//        this.mockMvc
//                .perform(post("/api/lobby/" + mockLobby.getId() + "/config")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(mockLobbyConfiguration.getDTO())))
//                .andExpect(status().isOk());
//    }

    //ToDo: Test if lobby exists in lobbyConfiguration.
//    @Test
//    public void configPostLobbyNotFoundTest() throws Exception {
//    }
    //ToDo: Test if user exists in lobbyConfiguration.
//    @Test
//    public void configPostUserNotFoundTest() throws Exception {
//    }
    //ToDo: Test if lobby status is 'CREATED' in lobbyConfiguration.
//    @Test
//    public void configPostLobbyNotCreatedTest() throws Exception {
//    }
    //ToDo: Test if user is not host in lobbyConfiguration.
//    @Test
//    public void configPostUserNotHostTest() throws Exception {
//    }

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
        when(userRepository.findByEmail(john.getEmail())).thenReturn(Optional.empty());

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
}
package server.api;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.game.GameStatus;
import java.util.Collections;
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
import server.database.entities.game.GamePlayer;
import server.database.entities.game.NormalGame;
import server.database.entities.game.configuration.GameConfiguration;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameRepository;
import server.services.GameService;

/**
 * Tests for the GameController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@EnableWebMvc
public class GameControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GameRepository gameRepository;

    @MockBean
    private GameService gameService;

    private UUID getUUID(int id) {
        return UUID.fromString("00000000-0000-0000-0000-00000000000" + (id % 10));
    }

    private Game game;
    private GameConfiguration gameConfiguration;
    private User john;
    private User susanne;
    private User sally;
    private GamePlayer johnPlayer;
    private GamePlayer susannePlayer;

    @Autowired
    public GameControllerTest(MockMvc mockMvc) {
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
        game = new NormalGame();
        game.setId(getUUID(3));
        game.setStatus(GameStatus.ONGOING);
        gameConfiguration = new NormalGameConfiguration(10, 10, 2);
        game.setConfiguration(gameConfiguration);

        // Add players
        johnPlayer = new GamePlayer(john);
        susannePlayer = new GamePlayer(susanne);
        susannePlayer.setAbandoned(true);
        game.add(johnPlayer);
        when(gameRepository.getPlayersGame(john.getId())).thenReturn(Optional.of(game));
        game.add(susannePlayer);
        when(gameRepository.getPlayersGame(susanne.getId())).thenReturn(Optional.of(game));

        // Mock the authentication
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        john.getEmail(),
                        john.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    @Test
    public void leaveOk() throws Exception {
        // Mock the service
        when(gameService.removePlayer(game, john)).thenReturn(true);

        // Perform the request
        this.mockMvc
                .perform(post("/api/game/leave"))
                .andExpect(status().isOk());

        // Verify interactions
        verify(gameRepository, times(1)).getPlayersGame(john.getId());
        verify(gameRepository, times(1)).save(game);
        verify(gameService, times(1)).removePlayer(game, john);
        verifyNoMoreInteractions(gameRepository, gameService);
    }

    @Test
    public void leaveNotFound() throws Exception {
        // Mock the authentication
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        sally.getEmail(),
                        sally.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

        // Perform the request
        this.mockMvc
                .perform(post("/api/game/leave"))
                .andExpect(status().isNotFound());

        // Verify interactions
        verify(gameRepository, times(1)).getPlayersGame(sally.getId());
        verifyNoMoreInteractions(gameRepository, gameService);
    }

    @Test
    public void leaveConflict() throws Exception {
        // Mock the authentication
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        susanne.getEmail(),
                        susanne.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

        // Mock the service
        when(gameService.removePlayer(game, susanne)).thenReturn(false);

        // Perform the request
        this.mockMvc
                .perform(post("/api/game/leave"))
                .andExpect(status().isConflict());

        // Verify interactions
        verify(gameRepository, times(1)).getPlayersGame(susanne.getId());
        verify(gameService, times(1)).removePlayer(game, susanne);
        verifyNoMoreInteractions(gameRepository, gameService);
    }
}

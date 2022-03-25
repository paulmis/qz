package server.api;

import static org.hamcrest.Matchers.equalToObject;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static server.utils.TestHelpers.getUUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.game.GameStatus;
import java.util.*;
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
import server.database.entities.question.MCQuestion;
import server.database.entities.question.Question;
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

    private Game<?> game;
    private GameConfiguration gameConfiguration;
    private User john;
    private User susanne;
    private User sally;
    private GamePlayer johnPlayer;
    private GamePlayer susannePlayer;
    private Question question;

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

        // Setup mock question
        question = new MCQuestion();

        // Create a lobby
        game = new NormalGame();
        game.setId(getUUID(3));
        game.setStatus(GameStatus.ONGOING);
        gameConfiguration = new NormalGameConfiguration(10, 10, 2);
        game.setConfiguration(gameConfiguration);
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

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
    public void questionFoundTest() throws Exception {
        // Request question object -> expect a ok status and mock question object
        game.addQuestions(new ArrayList<>(List.of(question)));
        game.setCurrentQuestionNumber(0);
        this.mockMvc
                .perform(get("/api/game/" + game.getId() + "/question"))
                .andExpect(status().isOk())
                .andExpect(content().string(equalToObject(
                        objectMapper.writeValueAsString(question.getDTO())
                )));
    }

    @Test
    public void questionEmptyTest() throws Exception {
        // Request question object -> expect a conflict status
        this.mockMvc
                .perform(get("/api/game/" + game.getId() + "/question"))
                .andExpect(status().isConflict());
    }

    @Test
    public void gameNotFoundTest() throws Exception {
        // Request question object -> expect a not found status
        this.mockMvc
                .perform(get("/api/game/" + getUUID(1) + "/question"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void leaveOk() throws Exception {
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
        doThrow(IllegalStateException.class).when(gameService).removePlayer(game, susanne);

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

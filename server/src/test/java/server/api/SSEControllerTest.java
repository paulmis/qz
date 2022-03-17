package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static server.TestHelpers.getUUID;

import commons.entities.game.GameDTO;
import commons.entities.game.GameStatus;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import server.database.entities.User;
import server.database.entities.game.Game;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameRepository;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWebMvc
class SSEControllerTest {
    private final MockMvc mockMvc;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GameRepository gameRepository;

    @Autowired
    public SSEControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(getUUID(1));
        user.setEmail("test@example.com");
        user.setPassword("test");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        ));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    static class MockGame extends Game {
        @Override
        public GameDTO getDTO() {
            return null;
        }
    }

    @Test
    void testOpenNotInGame() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/sse/open"))
                .andExpect(request().asyncStarted())
                .andDo(MockMvcResultHandlers.log())
                .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testOpenInGame() throws Exception {
        // Create a game
        Game game = new MockGame();
        game.setStatus(GameStatus.ONGOING);         // Set the game to ongoing

        when(gameRepository.getPlayersGame(user.getId()))
                .thenReturn(Optional.of(game));

        MvcResult mvcResult = this.mockMvc.perform(get("/api/sse/open"))
                .andExpect(request().asyncStarted())
                .andDo(MockMvcResultHandlers.log())
                .andReturn();

        // Run SSE async
        executor.execute(() -> {
            try {
                this.mockMvc.perform(asyncDispatch(mvcResult))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.TEXT_EVENT_STREAM));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Check if the connection was added properly.
        assertEquals(1, game.emitters.size());
        game.emitters.disconnectAll();  // Disconnect all the emitters to allow the async worker to terminate
        assertEquals(0, game.emitters.size());
    }
}
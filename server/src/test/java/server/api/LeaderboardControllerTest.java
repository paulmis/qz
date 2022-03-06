package server.api;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import server.database.entities.User;
import server.database.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWebMvc
class LeaderboardControllerTest {
    private final MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    public LeaderboardControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    private UUID getUUID(int id) {
        return UUID.fromString("00000000-0000-0000-0000-00000000000" + id);
    }

    private User getUser(int id, int gamesPlayed, int score) {
        User user = new User();
        user.setId(getUUID(id));
        user.setUsername("user" + id);
        user.setPassword("password" + id);
        user.setEmail("email" + id);
        user.setGamesPlayed(gamesPlayed);
        user.setScore(score);
        return user;
    }

    /**
     * Ensure that all required fields are returned by the /score leaderboard endpoint.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testRequiredFieldsPresentScore() throws Exception {
        List<User> users = List.of(getUser(1, 2, 3));
        when(userRepository.findAllByOrderByScoreDesc()).thenReturn(users);

        this.mockMvc.perform(get("/api/leaderboard/score"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].score").exists())
                .andExpect(jsonPath("$[0].gamesPlayed").exists())
                .andExpect(jsonPath("$[0].username").exists())
                .andExpect(jsonPath("$[0].id").exists());
    }

    /**
     * Ensure that sensitive fields are hidden (e-mail, password) in /score endpoint.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testSensitiveFieldsHiddenScore() throws Exception {
        List<User> users = List.of(getUser(1, 2, 3));
        when(userRepository.findAllByOrderByScoreDesc()).thenReturn(users);

        this.mockMvc.perform(get("/api/leaderboard/score"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").doesNotExist())
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    /**
     * Ensure that all required fields are returned by the /games leaderboard endpoint.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testRequiredFieldsPresentGamesPlayed() throws Exception {
        List<User> users = List.of(getUser(1, 2, 3));
        when(userRepository.findAllByOrderByGamesPlayedDesc()).thenReturn(users);

        this.mockMvc.perform(get("/api/leaderboard/games"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].score").exists())
                .andExpect(jsonPath("$[0].gamesPlayed").exists())
                .andExpect(jsonPath("$[0].username").exists())
                .andExpect(jsonPath("$[0].id").exists());
    }

    /**
     * Ensure that sensitive fields are hidden (e-mail, password) in /games endpoint.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testSensitiveFieldsHiddenGamesPlayed() throws Exception {
        List<User> users = List.of(getUser(1, 2, 3));
        when(userRepository.findAllByOrderByGamesPlayedDesc()).thenReturn(users);

        this.mockMvc.perform(get("/api/leaderboard/games"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").doesNotExist())
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    /**
     * Verify the behavior of the /score endpoint.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testScore() throws Exception {
        ArrayList<User> users = IntStream.iterate(3, i -> i > 0, i -> i - 1)
                .mapToObj(i -> getUser(i, i * 2, i * 3))
                .collect(Collectors.toCollection(ArrayList::new));
        when(userRepository.findAllByOrderByScoreDesc()).thenReturn(users);

        this.mockMvc.perform(get("/api/leaderboard/score"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].score").value(9))
                .andExpect(jsonPath("$[1].score").value(6))
                .andExpect(jsonPath("$[2].score").value(3));
    }

    /**
     * Verify the behavior of the /games endpoint.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testGamesPlayed() throws Exception {
        ArrayList<User> users = IntStream.iterate(3, i -> i > 0, i -> i - 1)
                .mapToObj(i -> getUser(i, i * 2, i * 3))
                .collect(Collectors.toCollection(ArrayList::new));
        when(userRepository.findAllByOrderByGamesPlayedDesc()).thenReturn(users);

        this.mockMvc.perform(get("/api/leaderboard/games"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].gamesPlayed").value(6))
                .andExpect(jsonPath("$[1].gamesPlayed").value(4))
                .andExpect(jsonPath("$[2].gamesPlayed").value(2));
    }

    /**
     * Verify the behavior of the /score endpoint when no players are found.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testNoPlayersGamesPlayed() throws Exception {
        when(userRepository.findAllByOrderByGamesPlayedDesc()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/api/leaderboard/games"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Verify the behavior of the /games endpoint when no players are found.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testNoPlayersScore() throws Exception {
        when(userRepository.findAllByOrderByGamesPlayedDesc()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/api/leaderboard/score"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }
}
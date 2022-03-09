package server.api;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static server.TestHelpers.getUUID;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
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

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @Autowired
    public LeaderboardControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
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
     * Ensure that default pagination parameters are properly set.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testDefaultPagination() throws Exception {
        when(userRepository.findAllByOrderByScoreDesc(pageableCaptor.capture())).thenReturn(new ArrayList<>());
        this.mockMvc.perform(get("/api/leaderboard/score"));
        // Test that the page number is correct.
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        // Test that the page size is correct.
        assertEquals(Integer.MAX_VALUE, pageableCaptor.getValue().getPageSize());
    }

    /**
     * Ensure that the pagination parameters are propagated properly to the JPA repository.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testPaginationPropagation() throws Exception {
        when(userRepository.findAllByOrderByScoreDesc(pageableCaptor.capture())).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/api/leaderboard/score")
                .param("page", "1")
                .param("size", "10"));
        // Test that the page number is correct.
        assertEquals(1, pageableCaptor.getValue().getPageNumber());
        // Test that the page size is correct.
        assertEquals(10, pageableCaptor.getValue().getPageSize());
    }

    /**
     * Ensure that all required fields are returned by the /score leaderboard endpoint.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testRequiredFieldsPresentScore() throws Exception {
        List<User> users = List.of(getUser(1, 2, 3));
        when(userRepository.findAllByOrderByScoreDesc(any(Pageable.class))).thenReturn(users);

        this.mockMvc.perform(get("/api/leaderboard/score"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                // Test that the score field is present.
                .andExpect(jsonPath("$[0].score").isNotEmpty())
                // Test that the gamesPlayed field is present.
                .andExpect(jsonPath("$[0].gamesPlayed").isNotEmpty())
                // Test that the username field is present.
                .andExpect(jsonPath("$[0].username").isNotEmpty())
                // Test that the id field is present.
                .andExpect(jsonPath("$[0].id").isNotEmpty());
    }

    /**
     * Ensure that sensitive fields are hidden (e-mail, password) in /score endpoint.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testSensitiveFieldsHiddenScore() throws Exception {
        List<User> users = List.of(getUser(1, 2, 3));
        when(userRepository.findAllByOrderByScoreDesc(any(Pageable.class))).thenReturn(users);

        this.mockMvc.perform(get("/api/leaderboard/score"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                // Test that the email field is hidden.
                .andExpect(jsonPath("$[0].email").doesNotExist())
                // Test that the password field is hidden.
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
        when(userRepository.findAllByOrderByGamesPlayedDesc(any(Pageable.class))).thenReturn(users);

        this.mockMvc.perform(get("/api/leaderboard/games"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                // Test that the score field is present.
                .andExpect(jsonPath("$[0].score").isNotEmpty())
                // Test that the gamesPlayed field is present.
                .andExpect(jsonPath("$[0].gamesPlayed").isNotEmpty())
                // Test that the username field is present.
                .andExpect(jsonPath("$[0].username").isNotEmpty())
                // Test that the id field is present.
                .andExpect(jsonPath("$[0].id").isNotEmpty());
    }

    /**
     * Ensure that sensitive fields are hidden (e-mail, password) in /games endpoint.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testSensitiveFieldsHiddenGamesPlayed() throws Exception {
        List<User> users = List.of(getUser(1, 2, 3));
        when(userRepository.findAllByOrderByGamesPlayedDesc(any(Pageable.class))).thenReturn(users);

        this.mockMvc.perform(get("/api/leaderboard/games"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                // Test that the email field is hidden.
                .andExpect(jsonPath("$[0].email").doesNotExist())
                // Test that the password field is hidden.
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
        when(userRepository.findAllByOrderByScoreDesc(any(Pageable.class))).thenReturn(users);

        this.mockMvc.perform(get("/api/leaderboard/score"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)))
                // Test that the score field for first user is correct.
                .andExpect(jsonPath("$[0].score").value(9))
                // Test that the score field for second user is correct.
                .andExpect(jsonPath("$[1].score").value(6))
                // Test that the score field for third user is correct.
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
        when(userRepository.findAllByOrderByGamesPlayedDesc(any(Pageable.class))).thenReturn(users);

        this.mockMvc.perform(get("/api/leaderboard/games"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)))
                // Test that the gamesPlayed field for first user is correct.
                .andExpect(jsonPath("$[0].gamesPlayed").value(6))
                // Test that the gamesPlayed field for second user is correct.
                .andExpect(jsonPath("$[1].gamesPlayed").value(4))
                // Test that the gamesPlayed field for third user is correct.
                .andExpect(jsonPath("$[2].gamesPlayed").value(2));
    }

    /**
     * Verify the behavior of the /score endpoint when no players are found.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testNoPlayersGamesPlayed() throws Exception {
        when(userRepository.findAllByOrderByGamesPlayedDesc(any(Pageable.class))).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/api/leaderboard/games"))
                // Test that the response is an empty array.
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Verify the behavior of the /games endpoint when no players are found.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testNoPlayersScore() throws Exception {
        when(userRepository.findAllByOrderByGamesPlayedDesc(any())).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/api/leaderboard/score"))
                // Test that the response is an empty array.
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }
}
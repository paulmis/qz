package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static server.TestHelpers.getUUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.utils.Views;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
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
import server.database.entities.utils.BaseEntity;
import server.database.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWebMvc
class LeaderboardControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @Autowired
    public LeaderboardControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    }

    private User getUser(int id, int gamesPlayed, int score) {
        User user = new User("user" + id,
                "email" + id,
                "password" + id,
                score,
                gamesPlayed,
                new HashSet<>());
        user.setId(getUUID(id));
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
        assertEquals(LeaderboardController.MAX_PAGE_SIZE, pageableCaptor.getValue().getPageSize());
    }

    /**
     * Ensure that the pagination parameters are propagated properly to the JPA repository.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testGetPaginationPropagation() throws Exception {
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

        String expectedResponse = objectMapper.writerWithView(Views.Public.class).writeValueAsString(
                users.stream().map(BaseEntity::getDTO).collect(Collectors.toList()));

        this.mockMvc.perform(get("/api/leaderboard/score"))
                .andExpect(status().isOk()).andExpect(content().json(expectedResponse));
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
                .andExpect(status().isOk())
                .andExpect(
                        content().json(
                                objectMapper
                                        .writerWithView(Views.Public.class)
                                        .writeValueAsString(
                                                users.stream()
                                                        .map(BaseEntity::getDTO)
                                                        .collect(Collectors.toList()))));
    }

    /**
     * Verify the behavior of the /score endpoint.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testScore() throws Exception {
        List<User> users = List.of(
                getUser(3, 6, 9),
                getUser(2, 4, 6),
                getUser(1, 2, 3));
        when(userRepository.findAllByOrderByScoreDesc(any(Pageable.class))).thenReturn(users);

        String expectedResponse = objectMapper.writerWithView(Views.Public.class).writeValueAsString(
                users.stream().map(BaseEntity::getDTO).collect(Collectors.toList()));

        this.mockMvc.perform(get("/api/leaderboard/score"))
                .andExpect(status().isOk()).andExpect(content().json(expectedResponse));
    }

    /**
     * Verify the behavior of the /games endpoint.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testGamesPlayed() throws Exception {
        List<User> users = List.of(
                getUser(3, 6, 9),
                getUser(2, 4, 6),
                getUser(1, 2, 3));

        when(userRepository.findAllByOrderByGamesPlayedDesc(any(Pageable.class))).thenReturn(users);

        String expectedResponse = objectMapper.writerWithView(Views.Public.class).writeValueAsString(
                users.stream().map(BaseEntity::getDTO).collect(Collectors.toList()));

        this.mockMvc.perform(get("/api/leaderboard/games"))
                .andExpect(status().isOk()).andExpect(content().json(expectedResponse));
    }

    /**
     * Verify the behavior of the /games endpoint when no players are found.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testNoPlayersGamesPlayed() throws Exception {
        when(userRepository.findAllByOrderByGamesPlayedDesc(any(Pageable.class))).thenReturn(new ArrayList<>());

        String expectedResponse = objectMapper.writerWithView(Views.Public.class).writeValueAsString(new ArrayList<>());

        this.mockMvc.perform(get("/api/leaderboard/games"))
                .andExpect(status().isOk()).andExpect(content().json(expectedResponse));
    }

    /**
     * Verify the behavior of the /score endpoint when no players are found.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testNoPlayersScore() throws Exception {
        when(userRepository.findAllByOrderByGamesPlayedDesc(any())).thenReturn(new ArrayList<>());

        String expectedResponse = objectMapper.writerWithView(Views.Public.class).writeValueAsString(new ArrayList<>());

        this.mockMvc.perform(get("/api/leaderboard/score"))
                .andExpect(status().isOk()).andExpect(content().json(expectedResponse));
    }
}
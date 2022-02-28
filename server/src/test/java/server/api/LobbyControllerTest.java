package server.api;

import static org.hamcrest.Matchers.equalToObject;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.entities.UserDTO;
import commons.entities.game.GamePlayerDTO;
import commons.entities.game.GameStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import server.database.entities.User;
import server.database.entities.game.Game;
import server.database.entities.game.GamePlayer;
import server.database.entities.question.Question;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameRepository;

@WebMvcTest(LobbyController.class)
class LobbyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameRepository gameRepoMock;

    @MockBean
    private UserRepository userRepoMock;

    // ToDo: remove this class when a subclass of game is implemented
    private class MockGame extends Game {
        @Override
        public Optional<Question> getNextQuestion() {
            return Optional.empty();
        }
    }

    @Test
    public void getAvailableLobbiesTest() throws Exception {
        // Mock a list of lobbies
        Game mockLobby = new MockGame();
        mockLobby.setId(UUID.randomUUID());
        mockLobby.setStatus(GameStatus.CREATED);
        Game mockLobby2 = new MockGame();
        mockLobby2.setId(UUID.randomUUID());
        mockLobby2.setStatus(GameStatus.CREATED);
        when(gameRepoMock.findAllByStatus(GameStatus.CREATED))
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
        // Mock a working gameId
        UUID lobbyId = UUID.randomUUID();
        Optional<Game> mockLobby = Optional.of(new MockGame());
        mockLobby.get().setId(lobbyId);
        mockLobby.get().setStatus(GameStatus.CREATED);
        when(gameRepoMock.findById(lobbyId)).thenReturn(mockLobby);

        // Request
        this.mockMvc.perform(get("/api/lobby/" + lobbyId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalToObject(
                        objectMapper.writeValueAsString(mockLobby.get().getDTO())
                )));
    }

    @Test
    public void lobbyNotFoundInfoTest() throws Exception {
        // Request
        this.mockMvc.perform(get("/api/lobby/" + UUID.randomUUID())).andExpect(status().isNotFound());
    }

    @Test
    public void joinOkTest() throws Exception {
        // Mock a working gameId
        UUID lobbyId = UUID.randomUUID();
        Optional<Game> mockLobby = Optional.of(new MockGame());
        mockLobby.get().setId(lobbyId);
        mockLobby.get().setStatus(GameStatus.CREATED);
        when(gameRepoMock.findById(lobbyId)).thenReturn(mockLobby);

        // Mock a working userId
        UUID userId = UUID.randomUUID();
        Optional<User> mockUser = Optional.of(new User());
        when(userRepoMock.findById(userId)).thenReturn(mockUser);

        // Mock a repository update
        when(gameRepoMock.save(any(Game.class))).thenReturn(null);

        // Request
        UserDTO player = new UserDTO();
        this.mockMvc.perform(
                put("/api/lobby/" + lobbyId + "/join/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player))
        ).andExpect(status().isOk());
    }

    @Test
    public void lobbyNotFoundJoinTest() throws Exception {
        // Mock a working userId
        UUID userId = UUID.randomUUID();
        Optional<User> mockUser = Optional.of(new User());
        when(userRepoMock.findById(userId)).thenReturn(mockUser);

        // Request
        GamePlayerDTO player = new GamePlayerDTO();
        this.mockMvc.perform(
                put("/api/lobby/" + UUID.randomUUID() + "/join/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player))
        ).andExpect(status().isNotFound());
    }

    @Test
    public void userNotFoundJoinTest() throws Exception {
        // Mock a working gameId
        UUID lobbyId = UUID.randomUUID();
        Optional<Game> mockLobby = Optional.of(new MockGame());
        mockLobby.get().setId(lobbyId);
        mockLobby.get().setStatus(GameStatus.CREATED);
        when(gameRepoMock.findById(lobbyId)).thenReturn(mockLobby);

        // Request
        GamePlayerDTO player = new GamePlayerDTO();
        this.mockMvc.perform(
                put("/api/lobby/" + UUID.randomUUID() + "/join/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player))
        ).andExpect(status().isNotFound());
    }

    @Test
    public void joinAlreadyPresentTest() throws Exception {
        // Mock a working gameId
        UUID lobbyId = UUID.randomUUID();
        Optional<Game> mockLobby = Optional.of(new MockGame());
        mockLobby.get().setId(lobbyId);
        mockLobby.get().setStatus(GameStatus.CREATED);
        when(gameRepoMock.findById(lobbyId)).thenReturn(mockLobby);

        // Mock a working userId
        UUID userId = UUID.randomUUID();
        Optional<User> mockUser = Optional.of(new User());
        when(userRepoMock.findById(userId)).thenReturn(mockUser);

        // Mock a joined player
        GamePlayerDTO player = new GamePlayerDTO();
        GamePlayer playerObj = new GamePlayer(player);
        playerObj.setUser(mockUser.get());
        mockLobby.get().add(playerObj);

        // Request
        this.mockMvc.perform(
                put("/api/lobby/" + lobbyId + "/join/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player))
        ).andExpect(status().isConflict());
    }

    @Test
    public void joinStartedGameTest() throws Exception {
        // Mock a working gameId
        UUID lobbyId = UUID.randomUUID();
        Optional<Game> mockLobby = Optional.of(new MockGame());
        mockLobby.get().setId(lobbyId);
        mockLobby.get().setStatus(GameStatus.ONGOING);
        when(gameRepoMock.findById(lobbyId)).thenReturn(mockLobby);

        // Mock a working userId
        UUID userId = UUID.randomUUID();
        Optional<User> mockUser = Optional.of(new User());
        when(userRepoMock.findById(userId)).thenReturn(mockUser);

        // Mock a repository update
        when(gameRepoMock.save(any(Game.class))).thenReturn(null);

        // Request
        UserDTO player = new UserDTO();
        this.mockMvc.perform(
                put("/api/lobby/" + lobbyId + "/join/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player))
        ).andExpect(status().isConflict());
    }
}
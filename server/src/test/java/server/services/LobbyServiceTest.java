package server.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static server.utils.TestHelpers.getUUID;

import commons.entities.messages.SSEMessage;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.entities.User;
import server.database.entities.game.GamePlayer;
import server.database.entities.game.NormalGame;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.database.entities.game.exceptions.LastPlayerRemovedException;
import server.database.repositories.game.GameRepository;

/**
 * Test class for the LobbyService.
 */
@ExtendWith(MockitoExtension.class)
public class LobbyServiceTest {
    @Mock
    private GameRepository gameRepository;

    @Mock
    private SSEManager sseManager;

    @InjectMocks
    private LobbyService lobbyService;

    NormalGame lobby;
    User joe;
    User susanne;
    User james;
    GamePlayer joePlayer;
    GamePlayer susannePlayer;
    GamePlayer jamesPlayer;

    @BeforeEach
    void init() throws IOException {
        // Create users
        joe = new User("joe", "joe@doe.com", "stinkywinky");
        joe.setId(getUUID(0));
        joePlayer = new GamePlayer(joe);
        joePlayer.setJoinDate(LocalDateTime.parse("2020-03-04T00:00:00"));

        susanne = new User("Susanne", "susanne@louisiane.com", "stinkymonkey");
        susanne.setId(getUUID(1));
        susannePlayer = new GamePlayer(susanne);
        susannePlayer.setJoinDate(LocalDateTime.parse("2022-03-03T00:00:00"));

        james = new User("James", "james@blames.com", "stinkydonkey");
        james.setId(getUUID(2));
        jamesPlayer = new GamePlayer(james);
        jamesPlayer.setJoinDate(LocalDateTime.parse("2022-03-02T00:00:00"));

        // Create the game
        lobby = new NormalGame();
        lobby.setId(getUUID(0));
        lobby.setGameId("aS33DB");
        lobby.setConfiguration(new NormalGameConfiguration(3, Duration.ofSeconds(13), 2, 2, 2f, 100, 0, 75));
        lobby.add(joePlayer);
        lobby.add(susannePlayer);

        lenient().when(sseManager.send(any(Iterable.class), any(SSEMessage.class))).thenReturn(true);
        lenient().when(sseManager.disconnect(any(Iterable.class))).thenReturn(true);
    }

    @Test
    void removePlayerOk() {
        // Mock the repository
        when(gameRepository.save(lobby)).thenReturn(lobby);

        // Call the service function
        assertTrue(lobbyService.removePlayer(lobby, joe));

        // Verify interactions
        verify(gameRepository, times(1)).save(lobby);
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    void removePlayerLast() throws LastPlayerRemovedException {
        // Call the service function
        lobby.remove(joe.getId());
        assertTrue(lobbyService.removePlayer(lobby, susanne));

        // Verify interactions
        verify(gameRepository, times(1)).delete(lobby);
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    void removePlayerNotFound() {
        // Call the service function
        assertFalse(lobbyService.removePlayer(lobby, james));

        // Verify interactions
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    void deleteLobbyOk() {
        // Call the service function
        assertTrue(lobbyService.deleteLobby(lobby, joe));

        // Verify interactions
        verify(gameRepository, times(1)).delete(lobby);
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    void deleteLobbyMissingHost() {
        // Remove lobby host
        lobby.setHost(null);

        // Call the service function
        assertTrue(lobbyService.deleteLobby(lobby, susanne));

        // Verify interactions
        verify(gameRepository, times(1)).delete(lobby);
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    void deleteLobbyNotHost() {
        // Call the service function
        assertFalse(lobbyService.deleteLobby(lobby, susanne));

        // Verify interactions
        verify(gameRepository, times(0)).delete(lobby);
        verifyNoMoreInteractions(gameRepository);
    }
}

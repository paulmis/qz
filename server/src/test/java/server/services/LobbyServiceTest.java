package server.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    void init() {
        // Create users
        joe = new User("joe", "joe@doe.com", "stinkywinky");
        joe.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        joePlayer = new GamePlayer(joe);
        joePlayer.setJoinDate(LocalDateTime.parse("2020-03-04T00:00:00"));

        susanne = new User("Susanne", "susanne@louisiane.com", "stinkymonkey");
        susanne.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        susannePlayer = new GamePlayer(susanne);
        susannePlayer.setJoinDate(LocalDateTime.parse("2022-03-03T00:00:00"));

        james = new User("James", "james@blames.com", "stinkydonkey");
        james.setId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        jamesPlayer = new GamePlayer(james);
        jamesPlayer.setJoinDate(LocalDateTime.parse("2022-03-02T00:00:00"));

        // Create the game
        lobby = new NormalGame();
        lobby.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        lobby.setConfiguration(new NormalGameConfiguration(3, 13, 2));
        lobby.add(joePlayer);
        lobby.add(susannePlayer);
    }

    @Test
    void removePlayerOk() {
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
}
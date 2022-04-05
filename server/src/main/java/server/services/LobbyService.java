package server.services;

import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.database.entities.User;
import server.database.entities.game.Game;
import server.database.entities.game.exceptions.LastPlayerRemovedException;
import server.database.repositories.game.GameRepository;

/**
 * Provides business logic for the lobbies.
 */
@Service
@Slf4j
public class LobbyService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private SSEManager sseManager;

    /**
     * Removes the specified user from the lobby. If this was the last player in the lobby, the game is deleted.
     *
     * @param lobby the lobby to remove the user from
     * @param user  the user to be removed
     * @return true if the user was removed, false otherwise
     */
    public boolean removePlayer(Game<?> lobby, User user) {
        // Remove the player from the lobby
        // If this was the last player, delete the lobby
        try {
            // Remove the user
            if (!lobby.remove(user.getId())) {
                return false;
            }

            // Save the lobby
            lobby = gameRepository.save(lobby);
            log.info("[{}] Removed player {}", lobby.getGameId(), user.getId());

            // Distribute the notifications to all players in the lobby
            sseManager.send(lobby.getUserIds(), new SSEMessage(SSEMessageType.LOBBY_MODIFIED));
        } catch (LastPlayerRemovedException ex) {
            gameRepository.delete(lobby);
            log.info("[{}] Last player removed, deleting lobby", lobby.getGameId());
        }
        return true;
    }

    /**
     * Deletes the lobby from the database. This action can be performed only by the lobby's host.
     *
     * @param lobby the lobby being deleted
     * @param user  the user performing the action
     * @return true if the lobby was successfully deleted, false otherwise
     */
    @Transactional(noRollbackFor = IOException.class)
    public boolean deleteLobby(Game<?> lobby, User user) {
        // Check if the host is set. If host is null, let anyone delete the lobby
        if (lobby.getHost() != null) {
            // Check that the user is the lobby host
            if (!lobby.getHost().getUser().equals(user)) {
                return false;
            }
        }

        // Delete the lobby
        gameRepository.delete(lobby);

        // Update players of the deletion
        sseManager.send(lobby.getUserIds(), new SSEMessage(SSEMessageType.LOBBY_DELETED));

        // Close connection to the players
        sseManager.disconnect(lobby.getUserIds());
        return true;
    }
}

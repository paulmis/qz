package server.services;

import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import java.io.IOException;
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
public class LobbyService {
    @Autowired
    private GameRepository gameRepository;

    /**
     * Removes the specified user from the lobby. If this was the last player in the lobby, the game is deleted.
     *
     * @param lobby the lobby to remove the user from
     * @param user the user to be removed
     * @return true if the user was removed, false otherwise
     */
    @Transactional
    public boolean removePlayer(Game lobby, User user) {
        // Remove the player from the lobby
        // If this was the last player, delete the lobby
        try {
            if (!lobby.remove(user.getId())) {
                return false;
            }
            gameRepository.save(lobby);
        } catch (LastPlayerRemovedException ex) {
            gameRepository.delete(lobby);
        }
        return true;
    }

    /**
     * Deletes the lobby from the database. This action can be performed only by the lobby's host.
     *
     * @param lobby the lobby being deleted
     * @param user the user performing the action
     * @return true if the lobby was successfully deleted, false otherwise
     */
    @Transactional(noRollbackFor = IOException.class)
    public boolean deleteLobby(Game lobby, User user) {
        // Check if the host is set. If host is null, let anyone can delete the lobby
        if (lobby.getHost() != null) {
            // Check that the user is the lobby host
            if (!lobby.getHost().getUser().equals(user)) {
                return false;
            }
        }

        // Delete the lobby
        gameRepository.delete(lobby);

        // Update players of the deletion
        try {
            lobby.getEmitters().sendAll(new SSEMessage(SSEMessageType.LOBBY_DELETED));
        } catch (IOException e) {
            // Couldn't notify other players, nothing to do
        }

        // Close connection to the players
        lobby.getEmitters().disconnectAll();
        return true;
    }
}

package server.services;

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
}

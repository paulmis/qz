package server.database.repositories.game;

import commons.entities.game.GameStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import server.database.entities.game.Game;

/**
 * JPA repository for interacting with Game entities in the database.
 */
public interface GameRepository extends JpaRepository<Game, UUID> {
    List<Game> findAllByStatus(GameStatus status);

    /**
     * Finds player's active lobby. This function should not be used for ongoing games.
     *
     * @param id user's id
     * @param status game status, should be set to CREATED
     * @return empty if there is no active lobby for the player, otherwise the lobby
     */
    Optional<Game> findByPlayers_User_IdEqualsAndStatus(@NonNull UUID id, @NonNull GameStatus status);

    /**
     * Finds player's active game.
     *
     * @param userId user's id
     * @return empty optional if there is no active game for the player, otherwise returns game
     */
    @Query("SELECT g FROM Game g "
            + "LEFT JOIN GamePlayer gp ON g.id = gp.game "
            + "WHERE gp.user = ?1 AND g.status = commons.entities.game.GameStatus.ONGOING AND gp.abandoned = false")
    Optional<Game> getPlayersGame(@NonNull UUID userId);
}

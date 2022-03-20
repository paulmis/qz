package server.database.repositories.game;

import commons.entities.game.GameStatus;

import java.util.Optional;
import java.util.UUID;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import server.database.entities.game.GamePlayer;

/**
 * JPA interface for accessing GamePlayer entities.
 */
public interface GamePlayerRepository extends JpaRepository<GamePlayer, UUID> {
    boolean existsByUserIdAndGameStatusNot(UUID userId, GameStatus status);

    boolean existsByUserIdAndGameId(UUID userId, UUID gameId);

    /**
     *
     * @param userId user's id
     * @return empty optional if the user is not a player, otherwise returns game player
     */
    @Query("SELECT gp FROM GamePlayer gp "
            + "WHERE gp.user.id = ?1")
    Optional<GamePlayer> findGamePlayerByUserId(@NonNull UUID userId);
}

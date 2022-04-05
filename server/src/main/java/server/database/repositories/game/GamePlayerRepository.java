package server.database.repositories.game;

import commons.entities.game.GameStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import server.database.entities.game.GamePlayer;

/**
 * JPA interface for accessing GamePlayer entities.
 */
public interface GamePlayerRepository extends JpaRepository<GamePlayer, UUID> {
    boolean existsByUserIdAndGameStatusNotAndAbandonedIsFalse(UUID userId, GameStatus status);

    boolean existsByUserIdAndGameId(UUID userId, UUID gameId);

    List<GamePlayer> findByGame_IdEqualsAndAbandonedIsFalseOrderByScoreDesc(@NonNull UUID id);
}

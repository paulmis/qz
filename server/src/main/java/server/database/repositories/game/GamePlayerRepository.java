package server.database.repositories.game;

import commons.entities.game.GameStatus;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import server.database.entities.game.GamePlayer;

/**
 * JPA interface for accessing GamePlayer entities.
 */
public interface GamePlayerRepository extends JpaRepository<GamePlayer, UUID> {
    boolean existsByUserIdAndGameStatusNot(UUID userId, GameStatus status);
}
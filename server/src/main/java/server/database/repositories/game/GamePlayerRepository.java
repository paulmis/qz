package server.database.repositories.game;

import org.springframework.data.jpa.repository.JpaRepository;
import server.database.entities.game.GamePlayer;

/**
 * JPA interface for accessing GamePlayer entities.
 */
public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
}
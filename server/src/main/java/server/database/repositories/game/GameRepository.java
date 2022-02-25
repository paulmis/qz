package server.database.repositories.game;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import server.database.entities.game.Game;
import server.database.entities.game.GameStatus;

/**
 * JPA repository for interacting with Game entities in the database.
 */
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findAllByStatus(GameStatus status);
}

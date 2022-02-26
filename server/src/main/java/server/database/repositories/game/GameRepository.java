package server.database.repositories.game;

import commons.entities.game.GameDTO;
import commons.entities.game.GameStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import server.database.entities.game.Game;

/**
 * JPA repository for interacting with Game entities in the database.
 */
public interface GameRepository extends JpaRepository<Game, UUID> {
    List<GameDTO> findAllByStatus(GameStatus status);
}

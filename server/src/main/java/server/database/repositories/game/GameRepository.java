package server.database.repositories.game;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import server.database.entities.game.Game;

/**
 * JPA repository for interacting with Game entities in the database.
 */
public interface GameRepository extends JpaRepository<Game, UUID> {}

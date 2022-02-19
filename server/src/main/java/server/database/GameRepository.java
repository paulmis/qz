package server.database;

import org.springframework.data.jpa.repository.JpaRepository;
import server.entities.game.gamemodes.Game;

/**
 * JPA repository for interacting with Game entities in the database.
 */
public interface GameRepository extends JpaRepository<Game, Long> {
}
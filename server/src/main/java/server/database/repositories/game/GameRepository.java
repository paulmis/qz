package server.database.repositories.game;

import commons.entities.game.GameStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import server.database.entities.game.Game;

/**
 * JPA repository for interacting with Game entities in the database.
 */
public interface GameRepository extends JpaRepository<Game, UUID> {
    List<Game> findAllByStatus(GameStatus status);

    Optional<Game> findByPlayers_User_EmailEqualsIgnoreCaseAndStatus(@NonNull String email, @NonNull GameStatus status);
}

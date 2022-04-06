package server.database.repositories.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import server.database.entities.game.Reaction;

/**
 * Repository for reactions.
 */
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    long deleteByNameEquals(@NonNull String name);
}
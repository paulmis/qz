package server.database.repositories.game;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import server.database.entities.game.configuration.GameConfiguration;

/**
 * JPA repository for fetching game configurations.
 */
public interface GameConfigurationRepository extends JpaRepository<GameConfiguration, UUID> {}
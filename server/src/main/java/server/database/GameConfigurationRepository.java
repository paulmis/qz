package server.database;

import org.springframework.data.jpa.repository.JpaRepository;
import server.database.entities.game.configuration.GameConfiguration;

/**
 * JPA repository for fetching game configurations.
 */
public interface GameConfigurationRepository extends JpaRepository<GameConfiguration, Long> {}
package server.database;

import server.entities.game.configuration.GameConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for fetching game configurations.
 */
public interface GameConfigurationRepository extends JpaRepository<GameConfiguration, Long> {}
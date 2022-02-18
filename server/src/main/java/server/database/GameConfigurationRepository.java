package server.database;

import commons.game.configuration.GameConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for fetching game configurations.
 */
public interface GameConfigurationRepository extends JpaRepository<GameConfiguration, Long> {}
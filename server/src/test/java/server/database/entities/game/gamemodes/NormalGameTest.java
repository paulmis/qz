package server.database.entities.game.gamemodes;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;
import server.database.entities.game.configuration.NormalGameConfiguration;

class NormalGameTest {
    /**
     * Check that the configuration is of correct type.
     */
    @Test
    void configurationType() {
        NormalGame game = new NormalGame();
        assertInstanceOf(NormalGameConfiguration.class, game.getConfiguration());
    }
}
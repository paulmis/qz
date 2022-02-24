package server.database.entities.game;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import commons.entities.game.GamePlayerDTO;
import org.junit.jupiter.api.Test;

class GamePlayerTest {
    @Test
    void testDTOConstructor() {
        GamePlayer gamePlayer = new GamePlayer(new GamePlayerDTO());
        assertNotNull(gamePlayer);
    }
}
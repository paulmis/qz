package server.database.entities.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GameTest {
    static class TestGameImplementation extends Game {
    }

    @Test
    void testAddNewPlayer() {
        TestGameImplementation game = new TestGameImplementation();
        GamePlayer gamePlayer = new GamePlayer(game);

        assertEquals(0, game.getPlayers().size());
        assertTrue(game.addPlayer(gamePlayer));
        assertEquals(1, game.getPlayers().size());
    }

    @Test
    void testAddNullPlayer() {
        TestGameImplementation game = new TestGameImplementation();

        assertEquals(0, game.getPlayers().size());
        assertThrows(NullPointerException.class, () -> game.addPlayer(null));
        assertEquals(0, game.getPlayers().size());
    }
}
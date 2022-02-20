package server.database.entities.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import server.database.entities.question.Question;

class GameTest {
    /**
     * Sample implementation of the abstract Game entity.
     */
    static class TestGameImplementation extends Game {
        /**
         * Get the next question in the game.
         *
         * @return The current question.
         */
        @Override
        public Optional<Question> getNextQuestion() {
            return Optional.empty();
        }
    }

    /**
     * Test adding a new player.
     */
    @Test
    void testAddNewPlayer() {
        TestGameImplementation game = new TestGameImplementation();

        GamePlayer gamePlayer1 = new GamePlayer(game);
        assertEquals(0, game.getPlayers().size());
        assertTrue(game.addPlayer(gamePlayer1));
        assertEquals(1, game.getPlayers().size());
    }

    /**
     * Test adding multiple new players.
     */
    @Test
    void testAddNewPlayerMultiple() {
        TestGameImplementation game = new TestGameImplementation();

        assertEquals(0, game.getPlayers().size());

        assertTrue(game.addPlayer(new GamePlayer(game)));
        assertEquals(1, game.getPlayers().size());

        assertTrue(game.addPlayer(new GamePlayer(game)));
        assertEquals(2, game.getPlayers().size());

        assertTrue(game.addPlayer(new GamePlayer(game)));
        assertEquals(3, game.getPlayers().size());

        assertTrue(game.addPlayer(new GamePlayer(game)));
        assertEquals(4, game.getPlayers().size());
    }

    /**
     * Test adding the same player twice.
     */
    @Test
    void testAddDuplicatePlayer() {
        TestGameImplementation game = new TestGameImplementation();

        GamePlayer gamePlayer1 = new GamePlayer(game);
        assertEquals(0, game.getPlayers().size());
        assertTrue(game.addPlayer(gamePlayer1));
        assertEquals(1, game.getPlayers().size());
        assertFalse(game.addPlayer(gamePlayer1));
        assertEquals(1, game.getPlayers().size());
    }

    /**
     * Test adding and removing a player.
     */
    @Test
    void testRemovePlayer() {
        TestGameImplementation game = new TestGameImplementation();

        GamePlayer gamePlayer1 = new GamePlayer(game);
        assertEquals(0, game.getPlayers().size());
        assertTrue(game.addPlayer(gamePlayer1));
        assertEquals(1, game.getPlayers().size());
        assertTrue(game.removePlayer(gamePlayer1));
        assertEquals(0, game.getPlayers().size());
    }

    /**
     * Test adding and removing multiple players.
     */
    @Test
    void testRemovePlayerMultiple() {
        TestGameImplementation game = new TestGameImplementation();

        GamePlayer gamePlayer1 = new GamePlayer(game);
        assertEquals(0, game.getPlayers().size());
        assertTrue(game.addPlayer(gamePlayer1));
        assertEquals(1, game.getPlayers().size());

        GamePlayer gamePlayer2 = new GamePlayer(game);
        assertTrue(game.addPlayer(gamePlayer2));
        assertEquals(2, game.getPlayers().size());

        GamePlayer gamePlayer3 = new GamePlayer(game);
        assertTrue(game.addPlayer(gamePlayer3));
        assertEquals(3, game.getPlayers().size());

        assertTrue(game.removePlayer(gamePlayer1));
        assertEquals(2, game.getPlayers().size());

        assertTrue(game.removePlayer(gamePlayer2));
        assertEquals(1, game.getPlayers().size());
    }

    /**
     * Test removing a player who is not in the game.
     */
    @Test
    void testRemovePlayerNotPresent() {
        TestGameImplementation game = new TestGameImplementation();

        GamePlayer gamePlayer1 = new GamePlayer(game);
        assertEquals(0, game.getPlayers().size());
        assertTrue(game.addPlayer(gamePlayer1));
        assertEquals(1, game.getPlayers().size());

        GamePlayer gamePlayer2 = new GamePlayer(game);
        assertFalse(game.removePlayer(gamePlayer2));
        assertEquals(1, game.getPlayers().size());
    }

    /**
     * Test adding a null player.
     */
    @Test
    void testAddNullPlayer() {
        TestGameImplementation game = new TestGameImplementation();

        assertEquals(0, game.getPlayers().size());
        assertThrows(NullPointerException.class, () -> game.addPlayer(null));
        assertEquals(0, game.getPlayers().size());
    }

    /**
     * Test removing a null player.
     */
    @Test
    void testRemoveNullPlayer() {
        TestGameImplementation game = new TestGameImplementation();

        assertEquals(0, game.getPlayers().size());
        assertThrows(NullPointerException.class, () -> game.removePlayer(null));
        assertEquals(0, game.getPlayers().size());
    }

    /**
     * Test the getQuestion function.
     */
    @Test
    void testGetQuestion() {
        TestGameImplementation game = new TestGameImplementation();

        assertEquals(Optional.empty(), game.getNextQuestion());
    }
}
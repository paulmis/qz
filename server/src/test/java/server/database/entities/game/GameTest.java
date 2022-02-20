package server.database.entities.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import server.database.entities.question.Question;

class GameTest {
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

    @Test
    void testAddNewPlayer() {
        TestGameImplementation game = new TestGameImplementation();

        GamePlayer gamePlayer1 = new GamePlayer(game);
        assertEquals(0, game.getPlayers().size());
        assertTrue(game.addPlayer(gamePlayer1));
        assertEquals(1, game.getPlayers().size());

        GamePlayer gamePlayer2 = new GamePlayer(game);
        assertTrue(game.addPlayer(gamePlayer2));

        assertEquals(2, game.getPlayers().size());
    }

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

    @Test
    void testRemovePlayer() {
        TestGameImplementation game = new TestGameImplementation();

        GamePlayer gamePlayer1 = new GamePlayer(game);
        assertEquals(0, game.getPlayers().size());
        assertTrue(game.addPlayer(gamePlayer1));
        assertEquals(1, game.getPlayers().size());

        GamePlayer gamePlayer2 = new GamePlayer(game);
        assertTrue(game.addPlayer(gamePlayer2));
        assertEquals(2, game.getPlayers().size());
        assertTrue(game.removePlayer(gamePlayer1));
        assertEquals(1, game.getPlayers().size());
    }

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

    @Test
    void testAddNullPlayer() {
        TestGameImplementation game = new TestGameImplementation();

        assertEquals(0, game.getPlayers().size());
        assertThrows(NullPointerException.class, () -> game.addPlayer(null));
        assertEquals(0, game.getPlayers().size());
    }

    @Test
    void testGetQuestion() {
        TestGameImplementation game = new TestGameImplementation();

        assertEquals(Optional.empty(), game.getNextQuestion());
    }
}
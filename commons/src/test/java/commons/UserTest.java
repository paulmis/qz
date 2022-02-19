package commons;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * Tests for the User entity.
 */

public class UserTest {
    @Test
    public void checkConstructor() {
        UUID playerid = UUID.randomUUID();
        User p1 = new User(playerid, "testplayer@gmail.com", "1234", 0, 0);
        assertEquals(p1.getEmail(), "testplayer@gmail.com");
    }
    @Test
    public void equalsTest() {
        UUID playerid = UUID.randomUUID();
        User a = new User(playerid, "testplayer@gmail.com", "1234", 0, 0);
        User b = new User(playerid, "testplayer@gmail.com", "1234", 0, 0);
        assertEquals(a, b);
    }

    @Test
    public void hashCodeTest() {
        UUID playerid = UUID.randomUUID();
        User a = new User(playerid, "testplayer@gmail.com", "1234", 0, 0);
        User b = new User(playerid, "testplayer2@gmail.com", "1234", 0, 0);
        assertNotEquals(a.hashCode(), b.hashCode());
    }
}

package server.database.entities;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import commons.entities.UserDTO;
import org.junit.jupiter.api.Test;

class UserTest {
    @Test
    void testConstructor() {
        User user = new User(new UserDTO());
        assertNotNull(user);
    }
}
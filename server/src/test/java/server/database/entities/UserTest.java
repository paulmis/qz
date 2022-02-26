package server.database.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import commons.entities.UserDTO;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserTest {
    @Test
    void testFromDTOConstructor() {
        UUID userID = UUID.randomUUID();
        String email = "test@example.com";
        String password = "AVerySecurePassword";

        UserDTO userDTO = new UserDTO();
        userDTO.setId(userID);
        userDTO.setEmail(email);
        userDTO.setPassword(password);

        User user = new User(userDTO);
        assertEquals(userID, user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
    }
}
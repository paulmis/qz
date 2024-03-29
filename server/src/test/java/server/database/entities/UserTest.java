package server.database.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import commons.entities.auth.UserDTO;
import org.junit.jupiter.api.Test;

class UserTest {
    @Test
    void testFromDTOConstructor() {
        UserDTO annDTO = new UserDTO(
                "Ann",
                "ann@damn.me",
                "AVerySecurePassword");

        User user = new User(annDTO);
        assertEquals(annDTO.getEmail(), user.getEmail());
        assertEquals(annDTO.getUsername(), user.getUsername());
        assertEquals(annDTO.getPassword(), user.getPassword());
    }
}
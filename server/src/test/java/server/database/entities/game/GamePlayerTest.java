package server.database.entities.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import commons.entities.UserDTO;
import commons.entities.game.GamePlayerDTO;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GamePlayerTest {
    @Test
    void testFromDTOConstructor() {
        UUID userID = UUID.randomUUID();
        String email = "test@example.com";
        String password = "AVerySecurePassword";
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userID);
        userDTO.setEmail(email);
        userDTO.setPassword(password);

        GamePlayerDTO gamePlayer = new GamePlayerDTO();
        gamePlayer.setUser(userDTO);

        GamePlayer gamePlayerEntity = new GamePlayer(gamePlayer);
        assertEquals(userID, gamePlayerEntity.getUser().getId());
    }
}
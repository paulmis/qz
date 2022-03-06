package server.database.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.game.NormalGameDTO;
import commons.entities.utils.DTO;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import server.database.entities.game.NormalGame;
import server.database.entities.game.configuration.NormalGameConfiguration;

/**
 * Tests for NormalGame class.
 */
public class NormalGameTest {
    @Test
    void convert() throws JsonProcessingException {
        // Create a ObjectMapper
        ObjectMapper om = new ObjectMapper().registerModule(new Jdk8Module());

        // Create a game
        NormalGame game = new NormalGame();
        game.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        game.setConfiguration(new NormalGameConfiguration(17, 13));

        // Serialize and deserialize
        String string = om.writeValueAsString(game.getDTO());
        DTO dto = om.readValue(string, NormalGameDTO.class);

        // Initiate a copy of the game
        NormalGame repl = new NormalGame((NormalGameDTO) dto);
        assertEquals(game, repl);
    }
}

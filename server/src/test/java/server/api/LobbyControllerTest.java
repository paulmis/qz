package server.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.entities.UserDTO;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LobbyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAvailableLobbiesTest() throws Exception {
        this.mockMvc.perform(get("/api/lobby/available")).andExpect(status().isOk());
    }

    @Test
    public void lobbyNotFoundInfoTest() throws Exception {
        this.mockMvc.perform(get("/api/lobby/" + UUID.randomUUID())).andExpect(status().isNotFound());
    }

    @Test
    public void lobbyNotFoundJoinTest() throws Exception {
        // ToDo: retrieve a working UserId
        UserDTO player = new UserDTO();
        this.mockMvc.perform(
                put("/api/lobby/" + UUID.randomUUID() + "/join/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player))
        ).andExpect(status().isNotFound());
    }

    @Test
    public void userNotFoundJoinTest() throws Exception {
        // ToDo: retrieve a working GameId for a lobby
        UserDTO player = new UserDTO();
        this.mockMvc.perform(
                put("/api/lobby/" + UUID.randomUUID() + "/join/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player))
        ).andExpect(status().isNotFound());
    }
}
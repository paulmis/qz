package server.api;

import static org.hamcrest.Matchers.equalToObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static server.utils.TestHelpers.getUUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.game.ReactionDTO;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import server.database.entities.User;
import server.database.entities.game.Game;
import server.database.entities.game.NormalGame;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameRepository;
import server.services.GameService;
import server.services.ReactionService;
import server.services.storage.StorageService;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWebMvc
class ReactionControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private ReactionService reactionService;

    @MockBean
    private StorageService storageService;

    @MockBean
    private GameService gameService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GameRepository gameRepository;

    @Captor
    private ArgumentCaptor<ReactionDTO> reactionCaptor;

    @Autowired
    public ReactionControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    }

    @Test
    void getReactions() throws Exception {
        when(reactionService.getReactionURLs())
                .thenReturn(Map.of("test", URI.create("https://youtube.com/watch?v=dQw4w9WgXcQ")));

        this.mockMvc.perform(get("/api/reaction"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        equalToObject(objectMapper.writeValueAsString(
                                Map.of("test", URI.create("https://youtube.com/watch?v=dQw4w9WgXcQ"))
                        ))
                ));
    }

    @Test
    void createReactionNameNull() throws Exception {
        ReactionDTO reaction = new ReactionDTO();

        MockMultipartFile file = new MockMultipartFile("image",
                "test.png",
                "image/png",
                "test".getBytes());
        MockMultipartFile name = new MockMultipartFile("reaction",
                "blob",
                "application/json",
                objectMapper.writeValueAsBytes(reaction));

        this.mockMvc.perform(multipart("/api/reaction").file(file).file(name))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReaction() throws Exception {
        when(storageService.store(any(InputStream.class))).thenReturn(getUUID(3));
        when(reactionService.addReaction(any(String.class), any(UUID.class))).thenReturn("test");

        ReactionDTO reaction = new ReactionDTO();
        reaction.setReactionType("test");

        MockMultipartFile file = new MockMultipartFile("image",
                "test.png",
                "image/png",
                "test".getBytes());
        MockMultipartFile name = new MockMultipartFile("reaction",
                "blob",
                "application/json",
                objectMapper.writeValueAsBytes(reaction));

        this.mockMvc.perform(multipart("/api/reaction").file(file).file(name))
                .andExpect(status().isOk());
    }

    @Test
    void deleteReaction() throws Exception {
        when(reactionService.removeReaction(any(String.class))).thenReturn(true);

        this.mockMvc.perform(delete("/api/reaction?reactionType=test"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteReactionNotFound() throws Exception {
        when(reactionService.removeReaction(any(String.class))).thenReturn(false);

        this.mockMvc.perform(delete("/api/reaction?reactionType=test"))
                .andExpect(status().isNotFound());
    }

    @Test
    void sendReaction() throws Exception {
        // Mock the authentication
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "test@example.com",
                        "password",
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

        User user = new User();
        user.setId(getUUID(3));

        when(userRepository.findByEmailIgnoreCase(any(String.class))).thenReturn(Optional.of(user));
        when(gameRepository.getPlayersGame(any(UUID.class))).thenReturn(Optional.of(new NormalGame()));
        when(gameService.sendReaction(any(Game.class), reactionCaptor.capture())).thenReturn(true);

        ReactionDTO reaction = new ReactionDTO();
        reaction.setReactionType("test");
        reaction.setUserId(getUUID(3));

        this.mockMvc.perform(post("/api/reaction/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reaction)))
                .andExpect(status().isOk());
        assertEquals(reaction, reactionCaptor.getValue());
    }
}
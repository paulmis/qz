package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static server.utils.TestHelpers.getUUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.entities.auth.LoginDTO;
import commons.entities.auth.UserDTO;
import commons.entities.game.GameStatus;
import java.io.InputStream;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import server.database.entities.User;
import server.database.entities.game.NormalGame;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameRepository;
import server.services.storage.StorageService;

/**
 * Tests for AuthController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@EnableWebMvc
public class AuthControllerTests {
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GameRepository gameRepository;

    @MockBean
    private StorageService storageService;

    User joe;
    UserDTO joeDTO;
    NormalGame game;
    NormalGameConfiguration gameConfiguration;

    /**
     * Initializes the test suite.
     *
     * @param mockMvc the auto-configured mock mvc
     */
    @Autowired
    public AuthControllerTests(MockMvc mockMvc) {
        this.mvc = mockMvc;
        this.objectMapper = new ObjectMapper().registerModules(new Jdk8Module(), new JavaTimeModule());
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    @BeforeEach
    void init() {
        // Initialize the user
        joe = new User("joe", "joe@doe.com", "stinkywinky");
        joe.setId(getUUID(0));
        joeDTO = joe.getDTO();
        joe.setPassword(passwordEncoder.encode(joe.getPassword()));

        game = new NormalGame();
        game.setId(getUUID(3));
        game.setStatus(GameStatus.ONGOING);
        gameConfiguration = new NormalGameConfiguration(10, Duration.ofSeconds(10), 2, 2, 2f, 100, 0, 75);
        game.setConfiguration(gameConfiguration);
    }

    @Test
    void registerOk() throws Exception {
        // Mock the repository
        when(userRepository.existsByEmailIgnoreCaseOrUsername(joeDTO.getEmail(), joeDTO.getUsername()))
            .thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(joe);

        // Convert the DTO to a MultipartFile (JSON)
        MockMultipartFile userMP = new MockMultipartFile(
                "userData",
                "blob",
                "application/json",
                objectMapper.writeValueAsString(joeDTO).getBytes());

        // Perform the request
        this.mvc
                .perform(multipart("/api/auth/register")
                        .file(userMP))
                .andExpect(status().isCreated());
    }

    @Test
    void registerImageOk() throws Exception {
        // Mock the repository
        when(userRepository.existsByEmailIgnoreCaseOrUsername(joeDTO.getEmail(), joeDTO.getUsername()))
                .thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(joe);

        // Convert the DTO to a MultipartFile (JSON)
        MockMultipartFile userMP = new MockMultipartFile(
                "userData",
                "blob",
                "application/json",
                objectMapper.writeValueAsString(joeDTO).getBytes());

        // Create a dummy image file
        MockMultipartFile imageMP = new MockMultipartFile(
                "image",
                "activity.png",
                "image/png",
                "imageContent".getBytes());

        // Mock the response from the storage service
        when(storageService.store(any(InputStream.class))).thenReturn(getUUID(3));

        // Perform the request
        this.mvc
                .perform(multipart("/api/auth/register")
                        .file(userMP)
                        .file(imageMP))
                .andExpect(status().isCreated());
    }

    @Test
    void registerConflict() throws Exception {
        // Mock the repository
        when(userRepository.existsByEmailIgnoreCaseOrUsername(joeDTO.getEmail(), joeDTO.getUsername()))
            .thenReturn(true);

        // Convert the DTO to a MultipartFile (JSON)
        MockMultipartFile userMP = new MockMultipartFile(
                "userData",
                "blob",
                "application/json",
                objectMapper.writeValueAsString(joeDTO).getBytes());

        // Perform the request
        this.mvc
                .perform(multipart("/api/auth/register")
                        .file(userMP))
                .andExpect(status().isConflict());
    }

    @Test
    void registerBadRequestShortPassword() throws Exception {
        // Remove the password
        joeDTO.setPassword("dog");

        // Perform the request
        this.mvc
                .perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joeDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginOk() throws Exception {
        // Mock the repository
        when(userRepository.findByEmailIgnoreCase(joe.getEmail())).thenReturn(Optional.of(joe));
        when(gameRepository.getPlayersLobbyOrGame(joe.getId())).thenReturn(Optional.of(game));

        // Perform the request
        MvcResult result = this.mvc
            .perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joeDTO)))
            .andExpect(status().isOk())
            .andReturn();

        // Verify that the returned value is correct
        LoginDTO loginDTO = objectMapper.readValue(result.getResponse().getContentAsString(), LoginDTO.class);
        assertEquals(game.getDTO(), loginDTO.getGame());
    }

    @Test
    void loginUnauthorized() throws Exception {
        // Mock the repository
        when(userRepository.findByEmailIgnoreCase(joe.getEmail())).thenReturn(Optional.empty());

        // Perform the request
        this.mvc
            .perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joeDTO)))
            .andExpect(status().isUnauthorized());
    }
}

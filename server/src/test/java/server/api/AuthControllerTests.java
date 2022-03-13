package server.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static server.TestHelpers.getUUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.UserDTO;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import server.database.entities.User;
import server.database.repositories.UserRepository;

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

    User joe;
    UserDTO joeDTO;

    /**
     * Initializes the test suite.
     *
     * @param mockMvc the auto-configured mock mvc
     */
    @Autowired
    public AuthControllerTests(MockMvc mockMvc) {
        this.mvc = mockMvc;
        this.objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    @BeforeEach
    void init() {
        joe = new User("joe", "joe@doe.com", "stinkywinky");
        joe.setId(getUUID(0));
        joeDTO = joe.getDTO();
        joe.setPassword(passwordEncoder.encode(joe.getPassword()));
    }

    @Test
    void registerOk() throws Exception {
        // Mock the repository
        when(userRepository.existsByEmailOrUsername(joeDTO.getEmail(), joeDTO.getUsername())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(joe);

        // Perform the request
        this.mvc
                .perform(
                        post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(joeDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerConflict() throws Exception {
        // Mock the repository
        when(userRepository.existsByEmailOrUsername(joeDTO.getEmail(), joeDTO.getUsername())).thenReturn(true);

        // Perform the request
        this.mvc
                .perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(joeDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void loginOk() throws Exception {
        // Mock the repository
        when(userRepository.findByEmail(joe.getEmail())).thenReturn(Optional.of(joe));

        // Perform the request
        this.mvc
                .perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(joeDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void loginUnauthorized() throws Exception {
        // Mock the repository
        when(userRepository.findByEmail(joe.getEmail())).thenReturn(Optional.empty());

        // Perform the request
        this.mvc
                .perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(joeDTO)))
                .andExpect(status().isUnauthorized());
    }
}

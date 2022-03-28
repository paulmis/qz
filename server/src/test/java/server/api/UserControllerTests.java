package server.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static server.utils.TestHelpers.getUUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.auth.UserDTO;
import commons.entities.utils.Views;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import server.database.entities.User;
import server.database.repositories.UserRepository;

/**
 * Tests for UserController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@EnableWebMvc
public class UserControllerTests {
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

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
    public UserControllerTests(MockMvc mockMvc) {
        this.mvc = mockMvc;
        this.objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    }

    @BeforeEach
    void init() {
        joe = new User("joe", "joe@doe.com", "stinkywinky");
        joe.setId(getUUID(0));
        joeDTO = joe.getDTO();

        // Set the context user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                    joe.getEmail(),
                    joe.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    @Test
    void getOk() throws Exception {
        // Mock the repository
        when(userRepository.findByEmail(joe.getEmail())).thenReturn(Optional.of(joe));

        // Perform the request
        MvcResult res = this.mvc
                .perform(
                        get("/api/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(joeDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        objectMapper.writerWithView(Views.Private.class).writeValueAsString(joe.getDTO())))
                .andReturn();
    }

    @Test
    void getNotFound() throws Exception {
        // Mock the repository
        when(userRepository.findByEmail(joe.getEmail())).thenReturn(Optional.empty());

        // Perform the request
        MvcResult res = this.mvc
                .perform(
                        get("/api/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(joeDTO)))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}

package server.database.entities.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import server.database.entities.User;
import server.database.entities.auth.config.AuthContext;

/**
 * Tests for AuthContext.
 */
public class AuthContextTests {
    @Test
    void get() {
        // Create the user
        User joe = new User("joe", "joe@doe.com", "stinkywinky");
        joe.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        // Set the context
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        joe.getEmail(),
                        joe.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

        // Get the context
        assertEquals(AuthContext.get(), joe.getEmail());
    }
}

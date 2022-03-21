package server.database.entities.auth.config;

import java.util.Collections;
import java.util.Optional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import server.database.entities.User;
import server.database.repositories.UserRepository;

/**
 * Implements the UserDetailsService providing DAOs for authentication manager.
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates an authentication DAO from given user's mail.
     *
     * @param email user's mail
     * @return authentication DAO
     * @throws UsernameNotFoundException if the user with the given mail doesn't exist
     */
    @Override
    public UserDetails loadUserByUsername(String email)
        throws UsernameNotFoundException {
        // Fetch the user from the database
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User with email " + email + " doesn't exist");
        }

        // Create the DAO
        return new org.springframework.security.core.userdetails.User(
                email,
                user.get().getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}

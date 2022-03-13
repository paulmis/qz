package server.api;

import commons.entities.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.entities.User;
import server.database.entities.auth.config.JWTHandler;
import server.database.repositories.UserRepository;

/**
 * Controller that handles authentication requests.
 */
@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTHandler handler;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Allows the user to register.
     *
     * @param userData the user data
     * @return 409 if the user with this data already exists, 201 and the auth token otherwise
     */
    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody UserDTO userData) {
        // If a user with this email or username already exists, return 409
        if (userRepository.existsByEmailOrUsername(userData.getEmail(), userData.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Salt the password and persist
        userData.setPassword(passwordEncoder.encode(userData.getPassword()));
        User user = userRepository.save(new User(userData));

        // Generate a JWT token and return it with 201
        return new ResponseEntity<>(handler.generateToken(user.getEmail()), HttpStatus.CREATED);
    }

    /**
     * Allows the user to log in.
     *
     * @param userData user's data
     * @return 400 if the request is malformed, 401 if the user is not found or the password is incorrect,
     *     200 and the auth token otherwise
     */
    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody UserDTO userData) {
        try {
            // TODO: allow authentication with both mail and username
            // Authenticate the user
            var authToken = new UsernamePasswordAuthenticationToken(userData.getEmail(), userData.getPassword());
            org.springframework.security.core.userdetails.User userPrincipal =
                    (org.springframework.security.core.userdetails.User)
                            (authenticationManager.authenticate(authToken)).getPrincipal();

            // Find the user
            User user = userRepository
                    .findByEmail(userPrincipal.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not present in the repository"));

            // Generate a JWT token and return it with 200
            return ResponseEntity.ok(handler.generateToken(user.getEmail()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

package server.api;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.auth.LoginDTO;
import commons.entities.auth.UserDTO;
import commons.entities.utils.Views;
import java.net.URI;
import java.util.Optional;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.api.exceptions.UserAlreadyExistsException;
import server.database.entities.User;
import server.database.entities.auth.config.JWTHandler;
import server.database.entities.game.Game;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameRepository;

/**
 * Controller that handles authentication requests.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private JWTHandler handler;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Allows the user to register.
     *
     * @param userData the user data
     * @return 409 if the user with this data already exists, 400 if the DTO is malformed or doesn't meet constraints,
     *      201 and the auth token otherwise
     */
    @PostMapping("register")
    @JsonView(Views.Private.class)
    public ResponseEntity<LoginDTO> register(@Valid @RequestBody UserDTO userData) {
        // If a user with this email or username already exists, return 409
        if (userRepository.existsByEmailIgnoreCaseOrUsername(userData.getEmail(), userData.getUsername())) {
            log.info("Could not create user with email {} and username {}: user already exists",
                    userData.getEmail(),
                    userData.getUsername());
            throw new UserAlreadyExistsException();
        }

        // Salt the password
        try {
            userData.setPassword(passwordEncoder.encode(userData.getPassword()));
        } catch (IllegalArgumentException ex) {
            log.info("Could not create user with email {} and username {}: password is not valid",
                    userData.getEmail(),
                    userData.getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Persist the user and return 201 with the user data
        User user = userRepository.save(new User(userData));

        log.info("Created user with email {} and username {}", user.getEmail(), user.getUsername());

        return ResponseEntity
                .created(URI.create("/api/user/" + user.getId()))
                .body(
                        new LoginDTO(
                                handler.generateToken(user.getEmail()),
                                null,
                                user.getDTO()));
    }

    /**
     * Allows the user to log in.
     *
     * @param userData user's data
     * @return 400 if the request is malformed, 401 if the user is not found or the password is incorrect,
     *     200 and the auth token otherwise
     */
    @PostMapping("login")
    @JsonView(Views.Private.class)
    public ResponseEntity<LoginDTO> login(@RequestBody UserDTO userData) {
        try {
            // TODO: allow authentication with both mail and username
            // Authenticate the user
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userData.getEmail(),
                    userData.getPassword());
            org.springframework.security.core.userdetails.User userPrincipal =
                    (org.springframework.security.core.userdetails.User)
                            (authenticationManager.authenticate(authToken)).getPrincipal();

            // Find the user
            Optional<User> user = userRepository
                    .findByEmailIgnoreCase(userPrincipal.getUsername());

            // If the user doesn't exist, return 401
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Find the current lobby or game
            Optional<Game> game = gameRepository.getPlayersLobbyOrGame(user.get().getId());

            log.info("User {} logged in", user.get().getEmail());

            // Return 200 and the user data
            return ResponseEntity.ok(
                    new LoginDTO(
                            handler.generateToken(user.get().getEmail()),
                            game.isEmpty() ? null : game.get().getDTO(),
                            user.get().getDTO()));
        } catch (AuthenticationException ex) {
            log.info("Could not login user with email {}: {}", userData.getEmail(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

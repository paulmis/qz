package server.api;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.auth.LoginDTO;
import commons.entities.auth.UserDTO;
import commons.entities.utils.Views;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.api.exceptions.UserAlreadyExistsException;
import server.database.entities.User;
import server.database.entities.auth.config.JWTHandler;
import server.database.entities.game.Game;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameRepository;
import server.services.storage.StorageService;

/**
 * Controller that handles authentication requests.
 */
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private StorageService storageService;

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
    public ResponseEntity<LoginDTO> register(
            @Valid @RequestPart UserDTO userData,
            @RequestPart(required = false) MultipartFile image) {
        // If a user with this email or username already exists, return 409
        if (userRepository.existsByEmailIgnoreCaseOrUsername(userData.getEmail(), userData.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        // Salt the password
        try {
            userData.setPassword(passwordEncoder.encode(userData.getPassword()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Store the profile picture, if present
        if (image != null) {
            try {
                // Save the image to the storage
                UUID imageId = storageService.store(image.getInputStream());
                log.trace("Stored image '{}' for user '{}'", imageId, userData.getId());

                // Set the image resource ID
                userData.setProfilePic(imageId);

            } catch (IOException e) {
                log.error("Failed to store the image for user '{}'", userData.getId(), e);
            }
        }

        // Persist the user and return 201 with the user data
        User user = userRepository.save(new User(userData));
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
            var authToken = new UsernamePasswordAuthenticationToken(userData.getEmail(), userData.getPassword());
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

            // Return 200 and the user data
            return ResponseEntity.ok(
                new LoginDTO(
                    handler.generateToken(user.get().getEmail()),
                    game.isEmpty() ? null : game.get().getDTO(),
                    user.get().getDTO()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

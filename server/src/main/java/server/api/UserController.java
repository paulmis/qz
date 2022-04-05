package server.api;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.auth.UserDTO;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import commons.entities.utils.Views;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.api.exceptions.UserNotFoundException;
import server.api.exceptions.UsernameInUseException;
import server.database.entities.User;
import server.database.entities.auth.config.AuthContext;
import server.database.entities.game.Game;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameRepository;
import server.services.SSEManager;

/**
 * Controller that provides user metadata.
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    SSEManager sseManager;

    /**
     * Shows details of the currently logged in user.
     *
     * @return details of the currently logged in user
     */
    @GetMapping
    @JsonView(value = Views.Private.class)
    public ResponseEntity<UserDTO> get() {
        log.trace("Getting user details");
        Optional<User> user = userRepository.findByEmailIgnoreCase(AuthContext.get());
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user.get().getDTO());
    }

    /**
     * Gets the details of an arbitrary user.
     *
     * @return details of the requested user.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getById(@PathVariable UUID userId) {
        log.trace("Getting user details");
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }
        return ResponseEntity.ok(user.get().getDTO());
    }

    /**
     * Changes the username of a user.
     *
     * @param newUsername the new username.
     * @return the response code.
     */
    @PostMapping("/username")
    public ResponseEntity<UserDTO> changeUsername(@RequestBody String newUsername) {
        User user = userRepository
                .findByEmailIgnoreCase(AuthContext.get())
                .orElseThrow(UserNotFoundException::new);

        // Send 200 if new username is the same as the old one.
        if (user.getUsername().equals(newUsername)) {
            return ResponseEntity.ok(user.getDTO());
        }

        // If a user with this username already exists, return username in use exception.
        if (userRepository.existsByUsername(newUsername)) {
            throw new UsernameInUseException("Username is already in use.");
        }



        // Set the new username
        user.setUsername(newUsername);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Validates the user
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        // Persist the username
        userRepository.save(user);

        Optional<Game> gameOptional = gameRepository.getPlayersLobbyOrGame(user.getId());
        if (gameOptional.isPresent()) {
            try {
                sseManager.send(gameOptional.get().getUserIds(), new SSEMessage(SSEMessageType.LOBBY_MODIFIED));
            } catch (IOException exception) {
                log.error("Error occurred while sending USERNAME_CHANGED event: " + exception);
            }
        }

        return ResponseEntity.ok(user.getDTO());
    }
}

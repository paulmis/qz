package server.api;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.auth.UserDTO;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import commons.entities.utils.Views;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        Optional<User> user = userRepository.findByEmailIgnoreCase(AuthContext.get());
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
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
    public ResponseEntity changeUsername(@RequestBody String newUsername) {
        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(AuthContext.get());
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();

        // Send 200 if new username is the same as the old one.
        if (user.getUsername().equals(newUsername)) {
            return ResponseEntity.ok(URI.create("/api/user/" + user.getId()));
        }

        // If a user with this username already exists, return 409
        if (userRepository.existsByUsername(newUsername)) {
            throw new UsernameInUseException();
        }

        // Set the new username
        user.setUsername(newUsername);

        // Persist the username and return 200
        userRepository.save(user);
        System.out.println(user.getId());
        Optional<Game> gameOptional = gameRepository.getPlayersLobbyOrGame(user.getId());
        if (gameOptional.isPresent()) {
            try {
                sseManager.send(gameOptional.get().getUserIds(), new SSEMessage(SSEMessageType.USERNAME_CHANGED));
            } catch (IOException exception) {
                log.error("Error occurred while sending USERNAME_CHANGED event: " + exception);
            }
        }
        return ResponseEntity.ok(URI.create("/api/user/" + user.getId()));
    }
}

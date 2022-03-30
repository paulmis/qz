package server.api;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.auth.LoginDTO;
import commons.entities.auth.UserDTO;
import commons.entities.utils.Views;

import java.net.URI;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.api.exceptions.UserAlreadyExistsException;
import server.api.exceptions.UsernameInUseException;
import server.database.entities.User;
import server.database.entities.auth.config.AuthContext;
import server.database.repositories.UserRepository;

/**
 * Controller that provides user metadata.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserRepository userRepository;

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
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameInUseException();
        }

        // Set the new username
        user.setUsername(newUsername);

        // Persist the username and return 200
        userRepository.save(user);
        return ResponseEntity.ok(URI.create("/api/user/" + user.getId()));
    }
}

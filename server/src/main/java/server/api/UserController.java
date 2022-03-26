package server.api;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.UserDTO;
import commons.entities.utils.Views;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
        Optional<User> user = userRepository.findByEmail(AuthContext.get());
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user.get().getDTO());
    }
}

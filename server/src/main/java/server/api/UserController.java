package server.api;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.entities.User;
import server.database.repositories.UserRepository;

/**
 * Controller that provides user metadata.
 */
@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    UserRepository userRepository;

    /**
     * Shows details of the currently logged in user.
     *
     * @return details of the currently logged in user
     */
    @GetMapping
    public ResponseEntity<User> getUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = userRepository.findByEmail(email);
        return ResponseEntity.ok(user.get());
    }
}

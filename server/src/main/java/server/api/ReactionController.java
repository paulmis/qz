package server.api;

import commons.entities.game.ReactionDTO;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.api.exceptions.GameNotFoundException;
import server.api.exceptions.UserNotFoundException;
import server.database.entities.User;
import server.database.entities.auth.config.AuthContext;
import server.database.entities.game.Game;
import server.database.entities.game.GamePlayer;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameRepository;
import server.services.GameService;
import server.services.ReactionService;
import server.services.storage.StorageService;

/**
 * Controller for the reaction endpoint.
 */
@Slf4j
@RestController
@RequestMapping("/api/reaction")
public class ReactionController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private StorageService storageService;

    /**
     * Get the list of all reactions and corresponding URLs.
     *
     * @return Map of all reactions and corresponding URLs.
     */
    @GetMapping
    public ResponseEntity<Map<String, URI>> getReactions() {
        return ResponseEntity.ok(reactionService.getReactionURLs());
    }

    /**
     * Create a new reaction with an image.
     *
     * @param reaction reaction to create.
     * @param image image of the reaction.
     * @return response.
     */
    @PostMapping
    public ResponseEntity<Void> createReaction(
            @RequestPart("reaction") ReactionDTO reaction,
            @RequestPart("image") MultipartFile image) {
        if (image.isEmpty() || reaction.getReactionType() == null) {
            log.warn("Reaction or image is empty");
            return ResponseEntity.badRequest().build();
        }
        try {
            UUID reactionImageId = storageService.store(image.getInputStream());
            reactionService.addReaction(reaction.getReactionType(), reactionImageId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Could not store image: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Send a reaction to a game.
     *
     * @param reaction Reaction to send.
     * @return response.
     */
    @PostMapping("/send")
    ResponseEntity sendReaction(@RequestBody ReactionDTO reaction) {
        User user = userRepository.findByEmailIgnoreCase(AuthContext.get()).orElseThrow(() -> {
            log.error("User not found: {}", AuthContext.get());
            return new UserNotFoundException();
        });

        // If the user isn't in a game, throw exception
        Game game = gameRepository.getPlayersGame(user.getId()).orElseThrow(() -> {
            log.error("User not in game: {}", AuthContext.get());
            return new GameNotFoundException();
        });

        // Set the user ID - prevent any spoofing
        reaction.setUserId(user.getId());
        gameService.sendReaction(game, reaction);
        return ResponseEntity.ok().build();
    }
}

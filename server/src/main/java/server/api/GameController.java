package server.api;

import commons.entities.questions.QuestionDTO;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.entities.User;
import server.database.entities.auth.config.AuthContext;
import server.database.entities.game.Game;
import server.database.entities.question.Question;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GamePlayerRepository;
import server.database.repositories.game.GameRepository;
import server.services.GameService;


/**
 * Controller that handles all game related REST requests.
 */
@RestController
@RequestMapping("/api/game")
public class GameController {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Endpoint to allow a player to leave the game, i.e. marks the player as abandoned.
     *
     * @return 409 if the player already left, 404 if the player isn't in a game, 200 otherwise
     */
    @PostMapping("/leave")
    ResponseEntity leave() {
        // If the user or the game don't exist, return 404
        Optional<User> user = userRepository.findByEmail(AuthContext.get());
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // If the user isn't in a game, return 404
        Optional<Game> game = gameRepository.getPlayersGame(user.get().getId());
        if (game.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Mark the player as abandoned
        try {
            gameService.removePlayer(game.get(), user.get());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        gameRepository.save(game.get());

        // Return 200
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint for retrieving the current question.
     *
     * @param gameId the UUID of the current game
     * @return information/object of the current question
     */
    @GetMapping("/{gameId}/question")
    ResponseEntity<QuestionDTO> currentQuestion(
            @PathVariable UUID gameId) {
        // Check if game exists.
        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<Question> question = game.get().getQuestion();
        // Check if question is not empty;
        if (question.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        // Send 200 status and payload if question exists.
        return ResponseEntity.ok(question.get().getDTO());
    }
}

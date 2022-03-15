package server.api;

import commons.entities.QuestionDTO;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.entities.game.Game;
import server.database.entities.question.Question;
import server.database.repositories.game.GameRepository;

/**
 * GameController, controller for all game-related API endpoints.
 */
@RestController
@RequestMapping("/api/game")
public class GameController {

    /**
     * Question repository import.
     */
    @Autowired
    private GameRepository gameRepository;

    /**
     * Endpoint for retrieving the current question.
     *
     * @param gameId the UUID of the current game
     * @return information/object of the current question
     */
    @GetMapping("/{gameId}/question/")
    ResponseEntity<QuestionDTO> currentQuestion(@PathVariable UUID gameId) {

        // Check if game exists
        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Get current question
        Optional<Question> question = game.get().getQuestion();

        // Check if question is not empty
        if (question.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // Send 200 status and payload if question exists
        return ResponseEntity.ok(question.get().getDTO());
    }
}

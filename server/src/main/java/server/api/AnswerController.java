package server.api;

import commons.entities.AnswerDTO;
import commons.entities.game.GameStatus;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.entities.User;
import server.database.entities.auth.config.AuthContext;
import server.database.entities.game.Game;
import server.database.entities.game.GamePlayer;
import server.database.entities.question.Question;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GamePlayerRepository;
import server.database.repositories.game.GameRepository;

/**
 * AnswerController, controller for all api endpoints of question answers.
 */
@RestController
@RequestMapping("/api/game")
public class AnswerController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    /**
     * Sends the users answers to the server.
     *
     * @param answerData Contains the players answer in AnswerDTO format
     * @param gameId This is the gameId of the game being played
     * @return ok status if successful, not found status if game doesn't exist
     */
    @PutMapping("/{gameId}/answer")
    public ResponseEntity<HttpStatus> userAnswer(
            @RequestBody AnswerDTO answerData,
            @PathVariable @NonNull UUID gameId) {
        //Check if game exists.
        if (!gameRepository.existsById(gameId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        //Send 200 status if answer is sent successfully.
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{gameId}")
    ResponseEntity<AnswerDTO> getCorrectAnswer(@PathVariable UUID gameId) {
        Optional<Game> game = gameRepository.findById(gameId);
        Optional<User> user = userRepository.findByEmail(AuthContext.get());

        // Check if game and player both exist
        if (!game.isPresent() || user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Check that the user is playing in the game
        if (!gamePlayerRepository.existsByUserIdAndGameId(user.get().getId(), game.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Retrieve current question
        Optional<Question> currentQuestion = game.get().getQuestion();

        // Check if game is active
        if (currentQuestion.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.ok(currentQuestion.get().getRightAnswer());
    }
}

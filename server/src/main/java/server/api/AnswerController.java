package server.api;

import commons.entities.AnswerDTO;
import commons.entities.game.GameStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
 * Controller that provides endpoints for the answers.
 */
@RestController
@RequestMapping("api/answer")
public class AnswerController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

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

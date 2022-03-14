package server.api;

import commons.entities.AnswerDTO;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.database.entities.Answer;
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
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    /**
     * Sends the users answers to the server.
     *
     * @param answerData Contains the players answer in AnswerDTO format
     * @param gameId     This is the gameId of the game being played
     * @return ok status if successful, not found status if game doesn't exist
     */
    @PutMapping("/{gameId}/answer")
    public ResponseEntity userAnswer(
            @RequestBody AnswerDTO answerData,
            @PathVariable UUID gameId) {

        // Retrieve game and user
        Optional<Game> game = gameRepository.findById(gameId);
        Optional<User> user = userRepository.findByEmail(AuthContext.get());

        // Check if game exists.
        if (game.isEmpty() || user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Find GamePlayer
        Optional<GamePlayer> player = game.get().getPlayers().stream()
                .filter(pl -> ((GamePlayer) pl).getUser().getId().equals(user.get().getId())).findFirst();
        if (player.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Update the answer
        if (game.get().addAnswer(new Answer(answerData), player.get())) {
            // Answer has been received successfully.
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    /**
     * Returns the correct answer to a question.
     *
     * @param gameId      id of the game being played
     * @param questionIdx index of the question to get the answer of.
     *                    If empty, the answer to the current question is sent
     * @return correct answer to the current question
     */
    @GetMapping("/{gameId}/answer")
    ResponseEntity<AnswerDTO> getCorrectAnswer(
            @PathVariable UUID gameId,
            @RequestParam(name = "idx") Optional<Integer> questionIdx) {

        Optional<Game> game = gameRepository.findById(gameId);
        Optional<User> user = userRepository.findByEmail(AuthContext.get());

        // Check if game exists
        if (game.isEmpty() || user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Check that the user is playing in the game
        if (!gamePlayerRepository.existsByUserIdAndGameId(user.get().getId(), game.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Retrieve current question
        Optional<Question> toAnswer = game.get().getQuestion();

        // If questionIdx is present and valid, select the requested question
        if (questionIdx.isPresent() && questionIdx.get() >= 0 && questionIdx.get() < game.get().getQuestions().size()) {
            toAnswer = Optional.of((Question) game.get().getQuestions().get(questionIdx.get()));
        }

        // Check if game is active
        return toAnswer
                .map(question -> ResponseEntity.ok(question.getRightAnswer().getDTO()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    /**
     * Returns the amount of points gained by the user in the current question.
     *
     * @param gameId id of the game being played
     * @return points gained for the current question
     */
    @GetMapping("/{gameId}/score")
    ResponseEntity<Integer> getScore(@PathVariable UUID gameId) {
        Optional<Game> game = gameRepository.findById(gameId);
        Optional<User> user = userRepository.findByEmail(AuthContext.get());

        // Check if game exists
        if (game.isEmpty() || user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Check that the user is playing in the game
        if (!gamePlayerRepository.existsByUserIdAndGameId(user.get().getId(), game.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Retrieve current question
        Optional<Question> currentQuestion = game.get().getQuestion();

        // ToDo: use the question to evaluate the player's score
        Integer tempPoints = 100;

        // Check if game is active
        return currentQuestion
                .map(question -> ResponseEntity.ok(tempPoints))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }
}

package server.api;

import commons.entities.AnswerDTO;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
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
import server.database.entities.User;
import server.database.entities.auth.config.AuthContext;
import server.database.entities.game.Game;
import server.database.entities.game.GamePlayer;
import server.database.entities.question.Question;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GamePlayerRepository;
import server.database.repositories.game.GameRepository;
import server.database.repositories.question.ActivityRepository;
import server.services.GameService;

/**
 * AnswerController, controller for all api endpoints of question answers.
 */
@Slf4j
@RestController
@RequestMapping("/api/game")
public class AnswerController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private ActivityRepository activityRepository;

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
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(AuthContext.get());

        // Check if game exists.
        if (gameOpt.isEmpty() || userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Game game = gameOpt.get();
        User user = userOpt.get();

        // Find GamePlayer
        GamePlayer gamePlayer = (GamePlayer) game.getPlayers().get(user.getId());
        if (gamePlayer == null) {
            log.warn("GamePlayer not found for user {}", user.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Check if the game is accepting answers.
        if (!game.isAcceptingAnswers()) {
            log.warn("Game {} is not accepting answers", game.getId());
            throw new IllegalStateException("Game is not accepting answers.");
        }

        // Check if question is correct
        Optional<Question> currentQuestion = game.getQuestion();
        if (currentQuestion.isEmpty()) {
            log.warn("No question found for game {}", game.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!currentQuestion.get().getId().equals(answerData.getQuestionId())) {
            // Trying to answer the wrong question
            log.warn("Trying to answer question {} with answer for question {}",
                    currentQuestion.get().getId(), answerData.getQuestionId());
            throw new IllegalArgumentException("Trying to answer the wrong question.");
        }

        // Update the answer
        // XXX: There might be a race condition here?
        if (gameService.addAnswer(game, gamePlayer, answerData)) {
            log.trace("[{}] Answer added to game (question {}).", gameId, currentQuestion.get().getId());
            // Answer has been received successfully.
            return ResponseEntity.ok().build();
        } else {
            log.warn("[{}] Could not add answer from user {} for question {}.",
                    game.getId(),
                    user.getId(),
                    answerData.getQuestionId());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
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
        Optional<User> user = userRepository.findByEmailIgnoreCase(AuthContext.get());

        // Check if game exists
        if (game.isEmpty() || user.isEmpty()) {
            log.debug("Game or user not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Check that the user is playing in the game
        if (!gamePlayerRepository.existsByUserIdAndGameId(user.get().getId(), game.get().getId())) {
            log.info("User {} is not playing in game {}", user.get().getId(), game.get().getId());
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
                .map(question -> {
                    log.trace("[{}] Sending correct answer for question {}.", gameId, question.getId());
                    return ResponseEntity.ok(question.getRightAnswer());
                })
                .orElseGet(() -> {
                    log.debug("[{}] No question found.", gameId);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                });
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
        Optional<User> user = userRepository.findByEmailIgnoreCase(AuthContext.get());

        // Check if game exists
        if (game.isEmpty() || user.isEmpty()) {
            log.debug("Game or user not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Check that the user is playing in the game
        if (!gamePlayerRepository.existsByUserIdAndGameId(user.get().getId(), game.get().getId())) {
            log.info("User {} is not playing in game {}", user.get().getId(), game.get().getId());
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

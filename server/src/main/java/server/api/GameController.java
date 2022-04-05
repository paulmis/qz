package server.api;

import commons.entities.game.GamePlayerDTO;
import commons.entities.game.PowerUp;
import commons.entities.game.Reaction;
import commons.entities.questions.QuestionDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.api.exceptions.GameNotFoundException;
import server.api.exceptions.PowerUpAlreadyUsedException;
import server.api.exceptions.SSEFailedException;
import server.api.exceptions.UserNotFoundException;
import server.database.entities.User;
import server.database.entities.auth.config.AuthContext;
import server.database.entities.game.Game;
import server.database.entities.game.GamePlayer;
import server.database.entities.question.Question;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GamePlayerRepository;
import server.database.repositories.game.GameRepository;
import server.services.GameService;


/**
 * Controller that handles all game related REST requests.
 */
@Slf4j
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
        User user = userRepository.findByEmailIgnoreCase(AuthContext.get()).orElseThrow(UserNotFoundException::new);

        // If the user isn't in a game, return 404
        Game game = gameRepository.getPlayersGame(user.getId()).orElseThrow(GameNotFoundException::new);

        // Mark the player as abandoned
        gameService.removePlayer(game, user);
        gameRepository.save(game);

        log.debug("User '{}' left game '{}'", user.getUsername(), game.getId());
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
    ResponseEntity<QuestionDTO> currentQuestion(@PathVariable UUID gameId) {
        // Check if game exists.
        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty()) {
            log.warn("User {} does not exist", AuthContext.get());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<Question> question = game.get().getQuestion();
        // Check if question is not empty;
        if (question.isEmpty()) {
            log.warn("Question does not exist on game {}", game.get().getId());
            throw new IllegalStateException("Question is empty");
        }
        // Send 200 status and payload if question exists.
        return ResponseEntity.ok(question.get().getDTO());
    }

    /**
     * Get the leaderboard for a specific game.
     *
     * @param gameId the UUID of the game to get the leaderboard for.
     * @return the leaderboard for the game.
     */
    @GetMapping("/{gameId}/leaderboard")
    ResponseEntity<List<GamePlayerDTO>> getGameLeaderboard(@PathVariable UUID gameId) {
        // Return the players in the game, sorted by score
        List<GamePlayerDTO> players = gamePlayerRepository
                .findByGame_IdEqualsAndAbandonedIsFalseOrderByScoreDesc(gameId)
                .stream().map(GamePlayer::getDTO).collect(Collectors.toList());
        return ResponseEntity.ok(players);
    }

    /**
     * Get the current question number of the game.
     *
     * @param gameId the UUID of the game to get the question number for.
     * @return the question number.
     */
    @GetMapping("/{gameId}/questionNumber")
    ResponseEntity<Integer> getQuestionNumber(@PathVariable UUID gameId) {
        return ResponseEntity.ok(gameRepository.getById(gameId).getCurrentQuestionNumber());
    }

    /**
     * Allow users to use powerups.
     *
     * @param powerUp powerup to use
     * @return 200 if the powerup was used successfully, 404 if the powerup was not found,
     * @throws SSEFailedException if the powerup failed to use
     */
    @PostMapping("/powerUp")
    ResponseEntity sendPowerUp(@RequestBody PowerUp powerUp) throws SSEFailedException {
        // If the user or the game don't exist, throw exception
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(AuthContext.get());
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException();
        }

        // If the user isn't in a game, throw exception
        Optional<Game> gameOpt = gameRepository.getPlayersGame(userOpt.get().getId());
        if (gameOpt.isEmpty()) {
            throw new GameNotFoundException();
        }

        User user = userOpt.get();
        Game game = gameOpt.get();

        GamePlayer gamePlayer = (GamePlayer) game.getPlayers().get(user.getId());

        // If power-up has already been used, throw exception
        if (gamePlayer.getUserPowerUps().keySet().contains(powerUp)) {
            throw new PowerUpAlreadyUsedException();
        }

        gameService.sendPowerUp(game, gamePlayer, powerUp);
        gamePlayer.getUserPowerUps().put(powerUp, game.getCurrentQuestionNumber());
        gameRepository.save(game);

        // Return 200
        return ResponseEntity.ok().build();
    }


    /**
     * Get the URL of the image for the specified activity.
     *
     * @param reaction Reaction to get.
     * @return URL of the image associated with the activity.
     */
    @PostMapping("/reaction")
    ResponseEntity sendReaction(@RequestBody Reaction reaction) throws SSEFailedException {
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(AuthContext.get());
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException();
        }

        // If the user isn't in a game, throw exception
        Optional<Game> gameOpt = gameRepository.getPlayersGame(userOpt.get().getId());
        if (gameOpt.isEmpty()) {
            throw new GameNotFoundException();
        }

        User user = userOpt.get();
        Game game = gameOpt.get();

        GamePlayer gamePlayer = (GamePlayer) game.getPlayers().get(user.getId());

        gameService.sendReaction(game, gamePlayer, reaction);
        return ResponseEntity.ok().build();
    }
}

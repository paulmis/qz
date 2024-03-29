package server.services;

import commons.entities.ActivityDTO;
import commons.entities.AnswerDTO;
import commons.entities.game.GameStatus;
import commons.entities.game.PowerUp;
import commons.entities.game.ReactionDTO;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import commons.entities.questions.QuestionDTO;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.api.exceptions.PowerUpDisabledException;
import server.configuration.quiz.QuizConfiguration;
import server.database.entities.User;
import server.database.entities.game.DefiniteGame;
import server.database.entities.game.Game;
import server.database.entities.game.GamePlayer;
import server.database.entities.game.exceptions.GameFinishedException;
import server.database.entities.game.exceptions.LastPlayerRemovedException;
import server.database.entities.question.Activity;
import server.database.entities.question.MCQuestion;
import server.database.entities.question.Question;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GamePlayerRepository;
import server.database.repositories.game.GameRepository;
import server.services.answer.AnswerCollection;
import server.services.fsm.DefiniteGameFSM;
import server.services.fsm.FSMContext;
import server.services.fsm.FSMState;
import server.services.fsm.GameFSM;

/**
 * Handles a specific game.
 */
@Service
@Slf4j
public class GameService {
    Map<UUID, Map<UUID, AnswerCollection>> allGameAnswers = new ConcurrentHashMap<>();

    @Autowired
    @Getter
    private QuizConfiguration quizConfiguration;

    @Autowired
    @Getter
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    @Getter
    private UserRepository userRepository;

    @Autowired
    @Getter
    private SSEManager sseManager;

    @Autowired
    private FSMManager fsmManager;

    @Autowired
    @Getter
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private ReactionService reactionService;

    /**
     * Starts a new game, by verifying the starting conditions and creating a questions set.
     *
     * @param game the game to start
     * @throws NotImplementedException if a game other than a definite game is started
     * @throws IllegalStateException   if the game is already started or there aren't enough questions
     * @throws IOException             if sending the GAME_START message fails
     */
    @Transactional
    public Game start(Game game)
            throws NotImplementedException, IllegalStateException {

        if (game.getConfiguration().getCapacity() > game.getPlayers().size()) {
            game.getConfiguration().setCapacity(game.getPlayers().size());
        }

        // Make sure that the lobby is full and not started
        if (game.getStatus() != GameStatus.CREATED || !game.isFull()) {
            log.debug("[{}] Cannot start game: game is not full or has already started.", game.getId());
            throw new IllegalStateException();
        }

        // Launch the game
        game.setStatus(GameStatus.ONGOING);

        // Initialize the answers collection
        allGameAnswers.put(game.getId(), new ConcurrentHashMap<>());

        // Initialize the game
        if (game instanceof DefiniteGame) {
            DefiniteGame definiteGame = (DefiniteGame) game;
            definiteGame.addQuestions(questionService.provideQuestions(definiteGame.getQuestionsCount()));
            definiteGame = gameRepository.save(definiteGame);

            // Create and start a finite state machine for the game.
            fsmManager.addFSM(definiteGame,
                    new DefiniteGameFSM(
                            definiteGame,
                            new FSMContext(this)));
            fsmManager.startFSM(definiteGame.getId());

            // Return the started game
            return definiteGame;
        } else {
            log.warn("[{}] Attempt to start an unsupported game type: {}", game.getId(), game.getClass().getName());
            throw new NotImplementedException("Starting games other than definite games is not yet supported.");
        }
    }

    /**
     * Marks the player as abandoned and disconnects their SSE emitter
     * If the last player abandoned the lobby, marks the game as finished.
     *
     * @param game the game to remove the player from
     * @param user the user to remove
     * @throws IllegalStateException if the player has already abandoned the game, or the player isn't in the game
     */
    @Transactional
    public void removePlayer(Game game, User user) throws IllegalStateException {
        try {
            // If the removal fails, the player has already abandoned the lobby
            if (!game.remove(user.getId())) {
                throw new IllegalStateException("The player has already abandoned the lobby.");
            }
        } catch (LastPlayerRemovedException ex) {
            // If the player was the last player, conclude the game
            game.setStatus(GameStatus.FINISHED);
        }

        // Disconnect the player and update clients
        sseManager.unregister(user.getId());
        sseManager.send(game.getUserIds(), new SSEMessage(SSEMessageType.PLAYER_LEFT, user.getId()));
    }

    /**
     * Transitions the game to the next question stage.
     *
     * @param game the game to transition
     */
    @Transactional
    public void nextQuestion(Game<?> game, Long delay) throws GameFinishedException {
        log.debug("[{}] Trying to move to next question", game.getId());

        // Check if the game should finish
        if (game.shouldFinish()) {
            log.info("[{}] Game should finish", game.getId());
            throw new GameFinishedException();
        }

        // Set the current question number
        game.incrementQuestion();
        game.setAcceptingAnswers(true);
        game = gameRepository.save(game);

        // Distribute the event to all players
        log.trace("[{}] FSM runnable: accepting answers enabled.", game.getId());
        sseManager.send(game.getUserIds(), new SSEMessage(SSEMessageType.START_QUESTION, delay));
    }

    /**
     * Transitions the game to the answer stage.
     *
     * @param game the game to transition
     */
    public boolean showAnswer(Game<?> game, Long delay) {
        // Disable answering
        game.setAcceptingAnswers(false);
        game = gameRepository.save(game);

        // Distribute the event to all players
        log.trace("[{}] FSM runnable: accepting answers disabled.", game.getId());

        return sseManager.send(game.getUserIds(), new SSEMessage(SSEMessageType.STOP_QUESTION, delay));
    }

    /**
     * Finishes the game.
     *
     * @param game the game to transition
     */
    public void finish(Game<?> game) {
        // Mark the game as finished
        game.setStatus(GameStatus.FINISHED);
        game = gameRepository.save(game);


        var leaderboard = game.getPlayers()
                .entrySet()
                .stream()
                .filter(uuidGamePlayerEntry -> !uuidGamePlayerEntry.getValue().isAbandoned())
                .sorted((o1, o2) -> Integer.compare(o1.getValue().getScore(), o1.getValue().getScore()))
                .collect(Collectors.toList());

        for (int i = 0; i < leaderboard.size(); i++) {
            var entry = leaderboard.get(i);
            User user = userRepository.findById(entry.getKey()).get();

            user.setGamesWon(user.getGamesWon() + (i == 0 ? 1 : 0));
            user.setScore(Math.max(user.getScore(), entry.getValue().getScore()));
            userRepository.save(user);
        }
        // Distribute the event to all players
        log.debug("[{}] Game is finished.", game.getId());
        sseManager.send(game.getUserIds(), new SSEMessage(SSEMessageType.GAME_END));
    }

    /**
     * Add an answer to a game.
     *
     * @param game       the game to add the answer to.
     * @param gamePlayer the player who submitted the answer.
     * @param answer     the answer to add.
     * @return whether the answer was added properly.
     */
    public boolean addAnswer(Game game, GamePlayer gamePlayer, AnswerDTO answer) {
        if (!game.getPlayerIds().contains(gamePlayer.getId())) {
            log.warn("[{}] Player {} tried to submit an answer, but is not in the game.",
                    game.getId(),
                    gamePlayer.getId());
            return false;
        }
        Question question = (Question) game.getQuestion().orElse(null);
        if (question == null) {
            log.warn("[{}] Player {} tried to submit an answer, but there is no question.",
                    game.getId(),
                    gamePlayer.getId());
            return false;
        }

        Map<UUID, AnswerCollection> gameAnswerCollections = allGameAnswers.get(game.getId());
        if (gameAnswerCollections == null) {
            log.warn("[{}] Player {} tried to submit an answer, but the game map is not present.",
                    game.getId(),
                    gamePlayer.getId());
            gameAnswerCollections = new ConcurrentHashMap<>();
            allGameAnswers.put(game.getId(), gameAnswerCollections);
        }

        AnswerCollection answerCollection = gameAnswerCollections.get(question.getId());
        if (answerCollection == null) {
            log.trace("[{}] Creating answer collection for question {}.", game.getId(), question.getId());
            answerCollection = new AnswerCollection();
            allGameAnswers.get(game.getId()).put(question.getId(), answerCollection);
        }

        answerCollection.addAnswer(gamePlayer.getId(), answer);
        return true;
    }

    /**
     * Get answers for a specific game's current question.
     *
     * @param game the game to get the answers for.
     * @return the answers for the game (current question).
     */
    public AnswerCollection getAnswers(Game game) {
        Question currentQuestion = (Question) game.getQuestion().orElse(null);
        if (currentQuestion == null) {
            return null;
        }
        return getAnswers(game, currentQuestion);
    }

    /**
     * Get answers for a specific question of a specific game.
     *
     * @param game     Game to get the answers for.
     * @param question Question to get the answers for.
     * @return The answers for the given game and question.
     */
    public AnswerCollection getAnswers(Game game, Question question) {
        Map<UUID, AnswerCollection> gameAnswerCollections = allGameAnswers.get(game.getId());
        if (gameAnswerCollections == null || question == null) {
            return null;
        }
        return gameAnswerCollections.get(question.getId());
    }

    /**
     * Update the game score.
     *
     * @param game the game to update the score for.
     */
    public void updateScores(Game game) {
        updateScores(game, (Question) game.getQuestion().get(), LocalDateTime.now());
    }

    /**
     * Update the game score to account for a specific question.
     *
     * @param game     game to update the scores for.
     * @param question question to update the scores for.
     */
    public void updateScores(Game game, Question question, LocalDateTime questionEndTime) {
        AnswerCollection answerCollection = getAnswers(game, question);
        if (answerCollection == null) {
            // If there are no answers, there's nothing to do
            log.error("[{}] No answer collection for question {}.", game.getId(), question.getId());
            return;
        }

        log.debug("[{}] Updating scores for question {}.", game.getId(), question.getId());

        // Update game score
        question.checkAnswer(answerCollection).entrySet().forEach(entry -> {
            Optional<GamePlayer> gamePlayer = game.getPlayers().values().stream()
                    .filter(p -> ((GamePlayer) p).getId().equals(entry.getKey())).findFirst();
            if (gamePlayer.isEmpty()) {
                return;
            }

            int score = game.computeBaseScore(entry.getValue());
            boolean isCorrect = score > game.getConfiguration().getCorrectAnswerThreshold();

            // The answer time stamp
            LocalDateTime answerTime = answerCollection.getAnswer(entry.getKey()).getAnswerTime();
            // Compute score based on the quickness of answering a question
            score = game.computeTimeBasedScore(score, answerTime, questionEndTime);

            // Update players' streak
            game.updateStreak(gamePlayer.get(), isCorrect);
            // Calculate the streak score
            int streakScore = game.computeStreakScore(gamePlayer.get(), score);

            //Apply double points power up
            game.applyScorePowerUpModifiers(gamePlayer.get(), streakScore, 2);
            // Persist the score changes
            gamePlayerRepository.save(gamePlayer.get());

            log.debug("[{}] player {} now has {} points", game.getId(), entry.getKey(), gamePlayer.get().getScore());
        });

        log.debug("[{}] Scores updated.", game.getId());
    }


    /**
     * Applies a power-up to a game.
     *
     * @param game    the game.
     * @param player  the player that sent the power-up
     * @param powerUp the power-up that is to be applied.
     */
    public void sendPowerUp(Game game, GamePlayer player, PowerUp powerUp) {
        GameFSM gameFSM = fsmManager.getFSM(game);
        // If the game is in a state other than a question, disallow the use of power ups
        if (gameFSM.getState() != FSMState.QUESTION) {
            throw new PowerUpDisabledException();
        }
        // Actions based on selected power ups
        switch (powerUp) {
            case HalveTime:
                var newDelay = (gameFSM.getFuture().getScheduledDate().getTime() - (new Date()).getTime()) / 2;

                // Doesn't let the user play this power-up if there are only 1 second left
                if (newDelay < 1000) {
                    throw new PowerUpDisabledException();
                }

                gameFSM.reprogramCurrentTask(Duration.ofMillis(newDelay));
                break;
            default:
                break;
        }
        log.info("Sending power-up " + powerUp.name() + " to game: " + game.getGameId());
        sseManager.send(game.getUserIds(), new SSEMessage(SSEMessageType.POWER_UP_PLAYED, powerUp));
    }

    /**
     * Sends a reaction to the players in a game.
     *
     * @param game the game.
     * @param reaction the reaction that is to be sent to the other players.
     * @return whether the reaction was sent successfully.
     */
    public boolean sendReaction(Game game, ReactionDTO reaction) {
        log.debug("Sending reaction {} to game {}", reaction.getReactionType(), game.getGameId());
        return sseManager.send(game.getUserIds(), new SSEMessage(SSEMessageType.REACTION, reaction));
    }
}

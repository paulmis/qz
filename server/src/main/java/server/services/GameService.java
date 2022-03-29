package server.services;

import commons.entities.AnswerDTO;
import commons.entities.game.GameStatus;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.configuration.quiz.QuizConfiguration;
import server.database.entities.User;
import server.database.entities.game.DefiniteGame;
import server.database.entities.game.Game;
import server.database.entities.game.GamePlayer;
import server.database.entities.game.exceptions.GameFinishedException;
import server.database.entities.game.exceptions.LastPlayerRemovedException;
import server.database.entities.question.Question;
import server.database.repositories.game.GamePlayerRepository;
import server.database.repositories.game.GameRepository;
import server.database.repositories.question.QuestionRepository;
import server.services.answer.AnswerCollection;
import server.services.fsm.DefiniteGameFSM;
import server.services.fsm.FSMContext;

/**
 * Get the questions for a specific game.
 */
@Service
@Slf4j
public class GameService {
    Map<UUID, Map<UUID, AnswerCollection>> allGameAnswers = new ConcurrentHashMap<>();

    @Autowired
    @Getter
    private QuizConfiguration quizConfiguration;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    @Getter
    private SSEManager sseManager;

    @Autowired
    private FSMManager fsmManager;

    @Autowired
    @Getter
    private ThreadPoolTaskScheduler taskScheduler;

    /**
     * Provides the specified amount of questions, excluding the specified questions.
     *
     * @param count         The amount of questions to return.
     * @param usedQuestions The questions to exclude.
     * @return Randomly chosen questions.
     * @throws IllegalStateException If the amount of questions to return is greater than the amount of
     *                               questions in the database.
     */
    public List<Question> provideQuestions(int count, List<Question> usedQuestions) throws IllegalStateException {
        // Check that there are enough questions
        if (questionRepository.count() < count + usedQuestions.size()) {
            throw new IllegalStateException("Not enough questions in the database.");
        }

        // Create a list of all the available questions
        /*
        List<Question> questions =
                questionRepository
                    .findByIdNotIn(
                            usedQuestions
                                    .stream()
                                    .map(Question::getId)
                                    .collect(Collectors.toList()));
         */
        // ToDo: fix QuestionRepository::findByIdNotIn
        List<UUID> usedIds = usedQuestions.stream().map(Question::getId).collect(Collectors.toList());
        List<Question> questions = questionRepository.findAll()
                .stream().filter(q -> !usedIds.contains(q.getId()))
                .collect(Collectors.toList());

        // Randomize the list and return the requested amount of questions
        Collections.shuffle(questions);
        return questions.subList(0, count);
    }

    /**
     * Starts a new game, by verifying the starting conditions and creating a questions set.
     *
     * @param game the game to start
     * @throws NotImplementedException if a game other than a definite game is started
     * @throws IllegalStateException   if the game is already started or there aren't enough questions
     * @throws IOException             if sending the GAME_START message fails
     */
    @Transactional
    public void startGame(Game game)
            throws NotImplementedException, IllegalStateException, IOException {
        // Make sure that the lobby is full and not started
        if (game.getStatus() != GameStatus.CREATED || !game.isFull()) {
            throw new IllegalStateException();
        }

        // Launch the game
        game.setStatus(GameStatus.ONGOING);

        // Initialize the answers collection
        allGameAnswers.put(game.getId(), new ConcurrentHashMap<>());

        // Initialize the questions
        if (game instanceof DefiniteGame) {
            DefiniteGame definiteGame = (DefiniteGame) game;
            definiteGame.addQuestions(provideQuestions(definiteGame.getQuestionsCount(), new ArrayList<>()));

            // Distribute the start event to all players
            sseManager.send(definiteGame.getUserIds(), new SSEMessage(SSEMessageType.GAME_START));

            // Create and start a finite state machine for the game.
            fsmManager.addFSM(definiteGame,
                    new DefiniteGameFSM(definiteGame,
                            new FSMContext(this)));
            fsmManager.startFSM(definiteGame);
        } else {
            throw new NotImplementedException("Starting games other than definite games is not yet supported.");
        }

        gameRepository.save(game);
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
        try {
            sseManager.send(game.getUserIds(), new SSEMessage(SSEMessageType.PLAYER_LEFT, user.getId()));
        } catch (IOException ex) {
            // Log failure to update clients
            log.error("Unable to send removePlayer message to all players", ex);
        }
    }

    /**
     * Transitions the game to the next question stage.
     *
     * @param game the game to transition
     * @throws IOException if an SSE connection send failed.
     */
    @Transactional
    public void nextQuestion(Game<?> game, Long delay)
            throws IOException, GameFinishedException {
        // Check if the game should finish
        if (game.shouldFinish()) {
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
     * @throws IOException if an SSE connection send failed.
     */
    public boolean showAnswer(Game<?> game, Long delay)
            throws IOException {
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
     * @throws IOException if an SSE connection send failed.
     */
    public void finish(Game<?> game)
            throws IOException {
        // Mark the game as finished
        game.setStatus(GameStatus.FINISHED);
        game = gameRepository.save(game);

        // Distribute the event to all players
        log.debug("[{}] Game is finished.", game.getId());
        sseManager.send(game.getUserIds(), new SSEMessage(SSEMessageType.GAME_END));
    }

    /**
     * Add an answer to a game.
     *
     * @param game the game to add the answer to.
     * @param gamePlayer the player who submitted the answer.
     * @param answer the answer to add.
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
     * @param game Game to get the answers for.
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
        updateScores(game, (Question) game.getQuestion().get());
    }

    /**
     * Update the game score to account for a specific question.
     *
     * @param game game to update the scores for.
     * @param question question to update the scores for.
     */
    public void updateScores(Game game, Question question) {
        AnswerCollection answerCollection = getAnswers(game, question);
        if (answerCollection == null) {
            log.error("[{}] No answer collection for question {}.", game.getId(), question.getId());
            throw new IllegalArgumentException("No answer collection for question " + question.getId());
        }

        Map<UUID, Integer> scores = question.checkAnswer(answerCollection).entrySet().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        e -> game.computeBaseScore(e.getValue())
                ));

        game.getPlayers().values().forEach(p -> {
            GamePlayer player = (GamePlayer) p;

            int score = Optional.ofNullable(scores.get(player.getId())).orElse(0);
            boolean isCorrect = score > game.getConfiguration().getCorrectAnswerThreshold();

            game.updateStreak(player, isCorrect);
            game.updatePowerUpPoints(player, isCorrect);

            int streakScore = game.computeStreakScore(player, score);
            player.setScore(player.getScore() + streakScore);

            // Persist the score changes
            gamePlayerRepository.save(player);
        });
    }
}

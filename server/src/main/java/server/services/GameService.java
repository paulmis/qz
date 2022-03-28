package server.services;

import commons.entities.game.GameStatus;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.api.exceptions.SSEFailedException;
import server.database.entities.User;
import server.database.entities.game.DefiniteGame;
import server.database.entities.game.Game;
import server.database.entities.game.exceptions.GameFinishedException;
import server.database.entities.game.exceptions.LastPlayerRemovedException;
import server.database.entities.question.Question;
import server.database.repositories.game.GameRepository;
import server.database.repositories.question.QuestionRepository;
import server.services.fsm.DefiniteGameFSM;
import server.services.fsm.FSMContext;
import server.services.fsm.GameFSM;

/**
 * Get the questions for a specific game.
 */
@Service
@Slf4j
public class GameService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private SSEManager sseManager;

    @Autowired
    private FSMManager fsmManager;

    @Autowired
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
     * @return the started game
     * @throws NotImplementedException if a game other than a definite game is started
     * @throws IllegalStateException   if the game is already started or there aren't enough questions
     * @throws IOException             if sending the GAME_START message fails
     */
    @Transactional
    public Game start(Game game)
            throws NotImplementedException, IllegalStateException, SSEFailedException {
        // Make sure that the lobby is full and not started
        if (game.getStatus() != GameStatus.CREATED || !game.isFull()) {
            throw new IllegalStateException();
        }

        // Launch the game
        game.setStatus(GameStatus.ONGOING);

        // Initialize the game
        if (game instanceof DefiniteGame) {
            // Initialize the questions
            DefiniteGame definiteGame = (DefiniteGame) game;
            definiteGame.addQuestions(provideQuestions(definiteGame.getQuestionsCount(), new ArrayList<>()));
            definiteGame = gameRepository.save(definiteGame);

            // Distribute the start event to all players
            sseManager.send(definiteGame.getPlayerIds(), new SSEMessage(SSEMessageType.GAME_START));

            // Create and start the FSM
            fsmManager.addFSM(definiteGame,
                new DefiniteGameFSM(
                    definiteGame,
                    new FSMContext(sseManager, this, taskScheduler)));
            fsmManager.startFSM(definiteGame.getId());

            // Return the started game
            return definiteGame;
        } else {
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
        try {
            sseManager.send(game.getPlayerIds(), new SSEMessage(SSEMessageType.PLAYER_LEFT, user.getId()));
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
        sseManager.send(game.getPlayerIds(), new SSEMessage(SSEMessageType.START_QUESTION, delay));
    }

    /**
     * Transitions the game to the answer stage.
     *
     * @param game the game to transition
     * @throws IOException if an SSE connection send failed.
     */
    public void showAnswer(Game<?> game, Long delay)
        throws IOException {
        // Disable answering
        game.setAcceptingAnswers(false);
        game = gameRepository.save(game);

        // Distribute the event to all players
        log.trace("[{}] FSM runnable: accepting answers disabled.", game.getId());
        sseManager.send(game.getPlayerIds(), new SSEMessage(SSEMessageType.STOP_QUESTION, delay));
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
        sseManager.send(game.getPlayerIds(), new SSEMessage(SSEMessageType.GAME_END));
    }
}

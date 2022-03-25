package server.services;

import commons.entities.game.GameStatus;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import server.database.entities.User;
import server.database.entities.game.DefiniteGame;
import server.database.entities.game.Game;
import server.database.entities.game.exceptions.LastPlayerRemovedException;
import server.database.entities.question.Question;
import server.database.repositories.question.QuestionRepository;

/**
 * Get the questions for a specific game.
 */
@Service
@Slf4j
public class GameService {
    @Autowired
    private QuestionRepository questionRepository;

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
     * @throws UnsupportedOperationException if a game other than a definite game is started
     * @throws IllegalStateException         if the game is already started or there aren't enough questions
     */
    @Transactional
    public void startGame(Game game)
            throws UnsupportedOperationException, IllegalStateException {
        // Make sure that the lobby is full and not started
        if (game.getStatus() != GameStatus.CREATED || !game.isFull()) {
            throw new IllegalStateException();
        }

        // Initialize the questions
        if (game instanceof DefiniteGame) {
            DefiniteGame definiteGame = (DefiniteGame) game;
            definiteGame.addQuestions(provideQuestions(definiteGame.getQuestionsCount(), new ArrayList<>()));
            definiteGame.setCurrentQuestionNumber(0);
        } else {
            throw new UnsupportedOperationException("Starting games other than definite games is not yet supported.");
        }

        game.setStatus(GameStatus.ONGOING);
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
                throw new IllegalStateException("Impossible to remove the player");
            }
        } catch (LastPlayerRemovedException ex) {
            // If the player was the last player, conclude the game
            game.setStatus(GameStatus.FINISHED);
        }

        // Disconnect the player and update clients
        game.getEmitters().disconnect(user.getId());
        try {
            game
                .getEmitters()
                .sendAll(new SSEMessage(SSEMessageType.PLAYER_LEFT, user.getId()));
        } catch (IOException ex) {
            // Log failure to update clients
            log.error("Unable to send removePlayer message to all players", ex);
        }
    }
}

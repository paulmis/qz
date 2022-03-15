package server.services;

import commons.entities.game.GameStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.entities.game.DefiniteGame;
import server.database.entities.game.Game;
import server.database.entities.question.Question;
import server.database.repositories.question.QuestionRepository;

/**
 * Get the questions for a specific game.
 */
@Service
public class GameService {
    @Autowired
    private QuestionRepository questionRepository;

    /**
     * Provides the specified amount of questions, excluding the specified questions.
     *
     * @param count The amount of questions to return.
     * @param usedQuestions The questions to exclude.
     * @return Randomly chosen questions.
     * @throws IllegalStateException If the amount of questions to return is greater than the amount of
     *      questions in the database.
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
        // ToDo: use a query instead of filter programmatically
        List<Question> questions = questionRepository.findAll()
                .stream()
                .filter(q -> !usedQuestions.contains(q))
                .collect(Collectors.toList());

        // Randomize the list and return the requested amount of questions
        Collections.shuffle(questions);

        if (questions.size() <= count) {
            throw new IllegalStateException("Not enough questions in the database.");
        }
        return questions.subList(0, count);
    }

    /**
     * Starts a new game, by verifying the starting conditions and creating a questions set.
     *
     * @param game the game to start
     * @throws UnsupportedOperationException if a game other than a definite game is started
     * @throws IllegalStateException if the game is already started or there aren't enough questions
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
        } else {
            throw new UnsupportedOperationException("Starting games other than definite games is not yet supported.");
        }

        game.setStatus(GameStatus.ONGOING);
    }
}

package server.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.entities.game.DefiniteGame;
import server.database.entities.game.Game;
import server.database.entities.game.GameStatus;
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
    List<Question> provideQuestions(int count, List<Question> usedQuestions) throws IllegalStateException {
        // Check that there are enough questions
        if (questionRepository.count() < count + usedQuestions.size()) {
            throw new IllegalStateException("Not enough questions in the database.");
        }

        // Create a list of all the available questions
        List<Question> questions = new ArrayList<>(
                questionRepository
                    .findAll());

        // Randomize the list and return the requested amount of questions
        Collections.shuffle(questions);
        return questions.subList(0, count);
    }

    /**
     * Starts a new game, by verifying the starting conditions and creating a questions set.
     *
     * @param game the game to start
     */
    void startGame(Game game) {
        // Initialize the questions
        if (game instanceof DefiniteGame) {
            DefiniteGame definiteGame = (DefiniteGame) game;
            definiteGame.addQuestions(provideQuestions(definiteGame.getQuestionsCount(), new ArrayList<>()));
        } else {
            throw new UnsupportedOperationException("Starting games other than definite games is not yet supported.");
        }

        // Set the game status to started
        game.setStatus(GameStatus.ONGOING);
    }
}

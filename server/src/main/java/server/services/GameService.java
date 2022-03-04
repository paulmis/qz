package server.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    Question getQuestion(Game game) {
        List<Question> questions = questionRepository.findAll();
        return questions.get(game.getRandom().getRandom(0, questions.size()));
    }
}

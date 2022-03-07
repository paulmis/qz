package server.services;

import commons.entities.AnswerDTO;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.entities.game.Game;
import server.database.entities.question.Question;
import server.database.repositories.game.GameRepository;

/**
 * Service to send answer related SSEs.
 */
@Service
public class AnswerService {

    @Autowired
    private GameRepository gameRepository;

    /**
     * Send correct answer using SSE.
     *
     * @param gameId id of the game that needs the answer.
     */
    public void sendAnswer(UUID gameId) {
        Optional<Game> myGame = gameRepository.findById(gameId);
        if (myGame.isPresent()) {
            // Retrieve current question
            Optional<Question> currentQuestion = myGame.get().getQuestion();
            if (!currentQuestion.isPresent()) {
                // Game is finished
                return;
            }
            AnswerDTO answer = currentQuestion.get().getRightAnswer();
            // ToDo: get SSEManager to have access to sending methods
            //myGame.get().getEmitters().sendAll(answer);
        }
    }
}

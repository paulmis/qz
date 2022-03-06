package server.services;

import commons.entities.AnswerDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.entities.game.Game;
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
        // ToDo: get SSEManager to have access to sending methods
        Optional<Game> myGame = gameRepository.findById(gameId);
        if (myGame.isPresent()) {
            List<UUID> players = myGame.get().getPlayers().stream().map(p -> p.getUser().getId())
                    .collect(Collectors.toList());
            // ToDo: get GameService to retrieve current question
            //AnswerDTO answer = myGame.get().getQuestions().get(myGame.get().CurrentQuestion()).getRightAnswer();
            // SSEManager::send(players, answer);
        }
    }
}

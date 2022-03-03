package server.api;

import commons.entities.ActivityDTO;
import commons.entities.AnswerDTO;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.entities.game.Game;
import server.database.repositories.game.GameRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * AnswerController, controller for all api endpoints of question answers
 */
@RestController
@RequestMapping("/api")
public class AnswerController {

    /**
     * Game repository
     */
    @Autowired
    private GameRepository gameRepository;

    @PostMapping("game/{gameId}/answer")
    public ResponseEntity<List<AnswerDTO>> answerQuestion(@RequestBody AnswerDTO answerData,
                                                          @PathVariable @NonNull UUID gameId) {
        /**
         * Check if game exists
         */
        Optional<Game> game = gameRepository.findById(gameId);
        if(!game.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // ToDo: send answer data to server
        /**
         * Send 200 status if answer is sent successfully
         */
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

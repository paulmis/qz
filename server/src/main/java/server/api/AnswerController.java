package server.api;

import commons.entities.AnswerDTO;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.repositories.game.GameRepository;

/**
 * AnswerController, controller for all api endpoints of question answers.
 */
@RestController
@RequestMapping("/api")
public class AnswerController {

    /**
     * Game repository import.
     */
    @Autowired
    private GameRepository gameRepository;

    /**
     * Sends the users answers to the server.
     *
     * @param answerData Contains the players answer in AnswerDTO format
     * @param gameId This is the gameId of the game being played
     * @return ok status if successful, not found status if game doesn't exist
     */
    @PostMapping("/game/{gameId}/answer")
    public ResponseEntity<HttpStatus> userAnswer(
            @RequestBody AnswerDTO answerData,
            @PathVariable @NonNull UUID gameId) {
        //Check if game exists.
        if (!gameRepository.existsById(gameId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        //Send 200 status if answer is sent successfully.
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
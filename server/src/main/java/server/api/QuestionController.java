package server.api;

import commons.entities.QuestionDTO;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.entities.game.Game;
import server.database.entities.question.Question;
import server.database.repositories.game.GameRepository;
import server.database.repositories.question.QuestionRepository;

/**
 * QuestionController, controller for all api endpoints of question.
 */
@RestController
@RequestMapping("/api")
public class QuestionController {

    /**
     * Question repository import.
     */
    @Autowired
    private GameRepository gameRepository;

    /**
     * Question repository import.
     */
    @Autowired
    private QuestionRepository questionRepository;

    /**
     * Endpoint for retrieving the current question.
     *
     * @param gameId the UUID of the current game
     * @return information/object of the current question
     */
    @GetMapping("/game/{gameId}/question/{questionId}")
    ResponseEntity<QuestionDTO> currentQuestion(
            @PathVariable @NonNull UUID gameId,
            @PathVariable @NonNull UUID questionId) {
        Optional<Game> game = gameRepository.findById(gameId);
        //Check if game exists.
        if (!game.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<Question> question = questionRepository.findById(questionId);
        //Check if question exists.
        if (!question.isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        //Send 200 status and payload if question exists.
        return ResponseEntity.ok(question.get().getDTO());
    }
}

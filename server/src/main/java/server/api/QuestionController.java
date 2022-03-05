package server.api;

import commons.entities.QuestionDTO;
import lombok.NonNull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import server.database.entities.question.Question;
import server.database.repositories.question.QuestionRepository;

/**
 * QuestionController, controller for all api endpoints of question.
 */
@RestController
@RequestMapping("api/questions")
public class QuestionController {

    /**
     * Question repository import.
     */
    @Autowired
    private QuestionRepository questionRepository;

    /**
     * Endpoint for retrieving the current question.
     *
     * @param questionId the UUID of the current question
     * @return information/object of the current question
     */
    @GetMapping("/{questionId}")
    ResponseEntity<QuestionDTO> currentQuestion(@PathVariable @NonNull UUID questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        //Check if question exists.
        if (!question.isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        //Send 200 status if question exists and sends the question payload back.
        return ResponseEntity.ok(question.get().getDTO());
    }
}

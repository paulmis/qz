package server.api;

import commons.entities.QuestionDTO;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.entities.question.Question;
import server.database.repositories.question.QuestionRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * QuestionController, controller for all api endpoints of question
 */
@RestController
@RequestMapping("api/questions")
public class QuestionController {

    /**
     * Question repository
     */
    @Autowired
    private QuestionRepository questionRepository;

    /**
     * Endpoint for retrieving the current question
     *
     * @param questionId the UUID of the current question
     * @return information/object of the current question
     */
    @GetMapping("/{questionId}")
    ResponseEntity<QuestionDTO> currentQuestion(@PathVariable @NonNull UUID questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        if(!question.isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(question.get().getDTO());
    }
}

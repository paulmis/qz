package server.api;

import commons.entities.QuestionDTO;
import lombok.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.entities.question.Question;
import server.database.repositories.question.QuestionRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * QuestionController, controller for all api endpoints of question creation
 */
@RestController
@RequestMapping("api/questions")
@Generated
public class QuestionController {

    /**
     * Question repository
     */
    @Autowired
    private QuestionRepository questionRepository;

    /**
     * Endpoint for current question
     *
     * @return the current question object
     */
    @GetMapping(path = {"","/questions/{num}"})
    ResponseEntity<QuestionDTO> currentQuestion(@PathVariable UUID id) {
        QuestionDTO currentQuestion = questionRepository.findById(id).get().getDTO();
        return new ResponseEntity<>(currentQuestion, HttpStatus.OK);
    }
}

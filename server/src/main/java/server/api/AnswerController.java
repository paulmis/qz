package server.api;

import commons.entities.AnswerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.repositories.question.QuestionRepository;

/**
 * AnswerController, controller for all api endpoints of question answers
 */
@RestController
//@RequestMapping("/api/answers")
public class AnswerController {

    /**
     * Question repository
     */
    @Autowired
    private QuestionRepository questionRepository;

//    @PostMapping("userAnswer")
//    public ResponseEntity<String> answerQuestion(@RequestBody AnswerDTO answerData) {
//
//    }

}

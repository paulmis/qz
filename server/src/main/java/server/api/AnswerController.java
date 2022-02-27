package server.api;

import commons.entities.AnswerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.repositories.question.QuestionRepository;

/**
 * AnswerController, controller for all api endpoints of question answers
 */
@RestController
@RequestMapping("/api/answers")
public class AnswerController {
}

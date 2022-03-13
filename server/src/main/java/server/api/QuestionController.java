package server.api;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.entities.question.Activity;
import server.database.entities.question.MCQuestion;
import server.database.entities.question.Question;
import server.database.repositories.question.QuestionRepository;
import server.services.ActivityService;

/**
 * Question management controller.
 */
@RestController
@RequestMapping("/api/question")
public class QuestionController {
    @Autowired
    private ActivityService activityService;

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * Create a new random question.
     *
     * @return Whether the question was successfully created.
     */
    @PutMapping("/mc/create")
    public ResponseEntity<HttpStatus> mcCreate() {
        List<Activity> activities = activityService.getActivities(4);

        Question question = new MCQuestion(activities,
                "Question X",
                activities.get(0));

        questionRepository.save(question);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

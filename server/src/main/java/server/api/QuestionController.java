package server.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @PutMapping("/mc")
    public ResponseEntity<HttpStatus> mcCreate(
        @RequestParam(value = "amount", required = false, defaultValue = "1") Integer amount) {
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            // Get random activities and pick one as an answer
            List<Activity> activities = activityService.getActivities(4);
            Activity answer = activities.get(new Random().ints(0, activities.size()).findFirst().getAsInt());

            // Create the question
            questions.add(new MCQuestion(
                new HashSet<>(activities),
                answer.getDescription(),
                answer));
        }

        // Save the questions and return success
        questionRepository.saveAll(questions);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

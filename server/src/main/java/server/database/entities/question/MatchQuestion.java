package server.database.entities.question;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * MatchQuestion data structure - describes a match question.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@Entity
public class MatchQuestion extends Question {

    /**
     * Constructor for the MatchQuestion class.
     *
     * @param id         the UUID of the question.
     * @param activities the list of activities that compose the question.
     * @param text       the description of the question.
     */
    public MatchQuestion(UUID id, List<Activity> activities, String text) {
        super(id, activities, text);
    }

    /**
     * Copy constructor for the MatchQuestion class.
     *
     * @param mq an instance of Question to copy.
     */
    public MatchQuestion(Question mq) {
        super(mq);
    }

    /**
     * checkAnswer, checks if the answer of a match question is correct.
     *
     * @param userAnswers list of answers provided by each user.
     *                    Each user should have a list of activities as answer.
     *                    The list is compared to the original question's list.
     * @return a value between 0 and 1 indicating the percentage of points each user should get.
     */
    @Override
    public List<Double> checkAnswer(List<Answer> userAnswers) throws IllegalArgumentException {
        if (userAnswers == null) {
            throw new IllegalArgumentException("NULL input");
        }
        List<Double> points = new ArrayList<>();
        for (Answer ans : userAnswers) {
            if (ans.getUserChoice().size() != getActivities().size()) {
                throw new IllegalArgumentException(
                        "The number of activities in the answer must be the same as the question.");
            }
            // Check if the order of answers corresponds to the order of questions
            double currentPoints = 0;
            double pointStep = 1.0 / getActivities().size();
            for (int idx = 0; idx < getActivities().size(); idx++) {
                if (getActivities().get(idx).getCost() == ans.getUserChoice().get(idx).getCost()) {
                    currentPoints += pointStep;
                }
            }
            if (currentPoints + pointStep > 1) {
                // This is to avoid rounding errors like 3*(1/3) != 1
                currentPoints = 1;
            }
            points.add(currentPoints);
        }
        return points;
    }
}

package server.database.entities.question;

import commons.entities.QuestionDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import server.database.entities.Answer;

/**
 * EstimateQuestion data structure - describes an estimate question.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class EstimateQuestion extends Question {

    /**
     * Constructor for the EstimateQuestion class.
     *
     * @param id         the UUID of the question.
     * @param activities the list of activities that compose the question.
     * @param text       the description of the question.
     */
    public EstimateQuestion(UUID id, List<Activity> activities, String text) {
        super(activities, text);
        this.setId(id);
    }

    /**
     * Copy constructor for the EstimateQuestion class.
     *
     * @param mq an instance of Question to copy.
     */
    public EstimateQuestion(Question mq) {
        super(mq);
    }

    /**
     * Construct a new entity from a DTO.
     *
     * @param dto DTO to map to entity.
     */
    public EstimateQuestion(QuestionDTO dto) {
        new ModelMapper().map(dto, this);
    }

    /**
     * checkAnswer, checks if the answer of an estimate question is correct.
     *
     * @param userAnswers list of answers provided by each user.
     *                    Each user should have a single activity as answer.
     * @return a value between 0 and 1 indicating the percentage of points each user should get.
     */
    @Override
    public List<Double> checkAnswer(List<Answer> userAnswers) throws IllegalArgumentException {
        if (userAnswers == null) {
            throw new IllegalArgumentException("NULL input");
        }

        List<Double> points = new ArrayList<>();
        // estimation error of each user
        List<Long> errors = new ArrayList<>();
        // all the different errors, sorted and unique
        Set<Long> sortedErrors = new TreeSet<>();

        // Get all estimation errors
        long target = getActivities().get(0).getCost();
        for (Answer ans : userAnswers) {
            if (ans.getResponse().size() != 1) {
                throw new IllegalArgumentException("There should be a single activity per answer.");
            }
            long userError = Math.abs(ans.getResponse().get(0).getCost() - target);
            errors.add(userError);
            sortedErrors.add(userError);
        }

        // For each user find their ranking
        double pointStep = 1.0 / (sortedErrors.size() - 1);
        for (long myError : errors) {
            double currentPoints = 1;
            for (long err : sortedErrors) {
                if (myError == err) {
                    break;
                }
                // the furthest a user is from the top rank the fewer points they get
                currentPoints -= pointStep;
            }
            if (currentPoints - pointStep < 0) {
                // This is to avoid rounding errors like 3*(1/3) != 1
                currentPoints = 0;
            }
            points.add(currentPoints);
        }

        return points;
    }

    @Override
    public Answer getRightAnswer() {
        Answer rightAnswer = new Answer();
        Activity toEstimate = new Activity();
        toEstimate.setCost(getActivities().get(0).getCost());
        rightAnswer.setResponse(List.of(toEstimate));
        return rightAnswer;
    }

    @Override
    public QuestionDTO getDTO() {
        return new ModelMapper().map(this, QuestionDTO.class);
    }
}

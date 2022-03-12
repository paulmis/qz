package server.database.entities.question;

import commons.entities.QuestionDTO;
import java.util.ArrayList;
import java.util.List;
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
 * OrderQuestion data structure - describes a match question.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OrderQuestion extends Question {

    /**
     * Construct a new entity from a DTO.
     *
     * @param dto DTO to map to entity.
     */
    public OrderQuestion(QuestionDTO dto) {
        new ModelMapper().map(dto, this);
    }

    /**
     * A boolean indicating whether the answer should be in increasing order.
     */
    protected boolean increasing = true;

    /**
     * Constructor for the OrderQuestion class.
     *
     * @param id         the UUID of the question.
     * @param activities the list of activities that compose the question.
     * @param text       the description of the question.
     * @param increasing if the user has to provide the answer in increasing order or not.
     */
    public OrderQuestion(UUID id, List<Activity> activities, String text, boolean increasing) {
        super(activities, text);
        this.setId(id);
        this.increasing = increasing;
    }

    /**
     * Copy constructor for the OrderQuestion class.
     *
     * @param q          an instance of Question to copy.
     * @param increasing if the user has to provide the answer in increasing order or not.
     */
    public OrderQuestion(Question q, boolean increasing) {
        super(q);
        this.increasing = increasing;
    }

    /**
     * checkAnswer, checks if the answer of an order question is correct.
     *
     * @param userAnswers list of answers provided by each user.
     *                    Each user should have a list activities as answer,
     *                    their order is checked to assign the points.
     * @return a value between 0 and 1 indicating the percentage of points each user should get.
     */
    @Override
    public List<Double> checkAnswer(List<Answer> userAnswers) throws IllegalArgumentException {
        if (userAnswers == null) {
            throw new IllegalArgumentException("NULL input");
        }
        List<Double> points = new ArrayList<>();

        for (Answer ans : userAnswers) {
            if (ans.getResponse().size() != getActivities().size()) {
                throw new IllegalArgumentException(
                        "The number of activities in the answer must be the same as the question.");
            }
            // Check if the order of answers' costs is correct
            int currentVal = ans.getResponse().get(0).getCost();
            double currentPoints = 0;
            double pointStep = 1.0 / (getActivities().size() - 1);
            if (increasing) {
                for (int idx = 1; idx < getActivities().size(); idx++) {
                    if (ans.getResponse().get(idx).getCost() >= ans.getResponse().get(idx - 1).getCost()) {
                        currentPoints += pointStep;
                    }
                }
            } else {
                for (int idx = 1; idx < getActivities().size(); idx++) {
                    if (ans.getResponse().get(idx).getCost() <= ans.getResponse().get(idx - 1).getCost()) {
                        currentPoints += pointStep;
                    }
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

    @Override
    public Answer getRightAnswer() {
        Answer rightAnswer = new Answer();
        rightAnswer.setResponse(getActivities());
        return rightAnswer;
    }
    
    @Override
    public QuestionDTO getDTO() {
        return new ModelMapper().map(this, QuestionDTO.class);
    }
}

package server.database.entities.question;

import commons.entities.questions.QuestionDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import server.database.entities.answer.Answer;
import server.utils.MathHelpers;

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

        for (Answer ans : userAnswers) {
            if (ans.getResponse().size() != 1) {
                throw new IllegalArgumentException("There should be a single activity per answer.");
            }
        }

        return userAnswers.stream().map(answer -> MathHelpers.calculatePercentage(answer.getResponse().get(0),
                getActivities().get(0).getCost())).collect(Collectors.toList());
    }

    @Override
    public Answer getRightAnswer() {
        Answer rightAnswer = new Answer();
        rightAnswer.setResponse(List.of(getActivities().get(0).getCost()));
        return rightAnswer;
    }

    @Override
    public QuestionDTO getDTO() {
        return new ModelMapper().map(this, QuestionDTO.class);
    }
}

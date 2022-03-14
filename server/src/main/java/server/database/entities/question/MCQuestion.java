package server.database.entities.question;

import commons.entities.QuestionDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import server.database.entities.Answer;

/**
 * MCQuestion data structure - describes a multiple choice question.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class MCQuestion extends Question {

    /**
     * Construct a new entity from a DTO.
     *
     * @param dto DTO to map to entity.
     */
    public MCQuestion(QuestionDTO dto) {
        new ModelMapper().map(dto, this);
    }

    /**
     * Activity corresponding to the correct answer.
     */
    @ManyToOne
    @JoinColumn(name = "answer_id")
    protected Activity answer;

    /**
     * Boolean to indicate if the user has to guess the energy consumption
     * or the corresponding activity.
     */
    protected boolean guessConsumption = true;

    /**
     * Constructor for the MCQuestion class.
     *
     * @param id               the UUID of the question.
     * @param activities       the list of activities that compose the question.
     * @param text             the description of the question.
     * @param answer           the Activity that corresponds to the correct answer.
     * @param guessConsumption if the user has to guess the energy consumption of the activity
     *                         or the activity with a given consumption.
     */
    public MCQuestion(UUID id, List<Activity> activities, String text, Activity answer, boolean guessConsumption) {
        super(activities, text);
        this.setId(id);
        this.answer = answer;
        this.guessConsumption = guessConsumption;
    }

    /**
     * Copy constructor for the MCQuestion class.
     *
     * @param q                an instance of Question to copy.
     * @param answer           the Activity that corresponds to the correct answer.
     * @param guessConsumption if the user has to guess the energy consumption of the activity
     *                         or the activity with a given consumption.
     */
    public MCQuestion(Question q, Activity answer, boolean guessConsumption) {
        super(q);
        this.answer = answer;
        this.guessConsumption = guessConsumption;
    }

    /**
     * Constructor with only required fields.
     *
     * @param activities the list of activities that compose the question.
     * @param text       the description of the question.
     * @param answer    the Activity that corresponds to the correct answer.
     */
    public MCQuestion(List<Activity> activities, String text, Activity answer) {
        super(activities, text);
        this.answer = answer;
    }

    /**
     * checkAnswer, checks if the answer of a multiple choice question is correct.
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
        for (Answer ans : userAnswers) {
            // There should be a single activity per answer
            if (ans.getResponse().size() != 1) {
                throw new IllegalArgumentException("There should be a single activity per answer.");
            }
            // Only the cost is compared because different activities might have the same cost unbeknown to the user
            if (answer.getCost() == ans.getResponse().get(0).getCost()) {
                points.add(1.0);
            } else {
                points.add(0.0);
            }
        }
        return points;
    }

    @Override
    public Answer getRightAnswer() {
        Answer rightAnswer = new Answer();
        rightAnswer.setResponse(List.of(getAnswer()));
        return rightAnswer;
    }
    
    @Override
    public QuestionDTO getDTO() {
        return new ModelMapper().map(this, QuestionDTO.class);
    }
}

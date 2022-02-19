package commons;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * MCQuestion data structure - describes a multiple choice question.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class MCQuestion extends Question {

    /**
     * Activity corresponding to the correct answer.
     */
    @ManyToOne
    @JoinColumn(name = "answer_id")
    public Activity answer;

    /**
     * Boolean to indicate if the user has to guess the energy consumption
     * or the corresponding activity.
     */
    public boolean guessConsumption = true;

    /**
     * Copy constructor for the MC_Question class.
     *
     * @param q                an instance of Question to copy.
     * @param answer           the Activity that corresponds to the correct answer.
     * @param guessConsumption if the user has to guess the energy consumption of the activity
     *                         or the activity with a given consumption.
     */
    MCQuestion(Question q, Activity answer, boolean guessConsumption) {
        super(q);
        this.answer = answer;
        this.guessConsumption = guessConsumption;
    }

    @Override
    public List<Double> checkAnswer(List<Answer> userAnswers) throws IllegalArgumentException {
        List<Double> points = new ArrayList<>();
        for (Answer ans : userAnswers) {
            // There should be a single activity per answer
            if (ans.getUserChoice().size() != 1) {
                throw new IllegalArgumentException("There should be a single activity per answer.");
            }
            // Only the cost is compared because different activities might have the same cost unbeknown to the user
            if (answer.getCost() == ans.getUserChoice().get(0).getCost()) {
                points.add(1.0);
            } else {
                points.add(0.0);
            }
        }
        return points;
    }
}

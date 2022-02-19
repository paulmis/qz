package commons;

import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * MCQuestion data structure - describes a multiple choice question.
 */
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("MC")
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
    public boolean guessConsumption;

    MCQuestion(Question q, Activity answer, boolean guessConsumption) {
        super(q);
        this.answer = answer;
        this.guessConsumption = guessConsumption;
    }

    @Override
    public boolean checkAnswer(List<Activity> userAnswer) {
        // There should be a single answer in this list
        if (userAnswer.size() != 1) {
            return false;
        }
        /*
        NB! This assumes the offered options will always have different cost values.
        Otherwise, a user might select the correct value while it is associated to a different activity
         */
        return answer.equals(userAnswer.get(0));
    }
}

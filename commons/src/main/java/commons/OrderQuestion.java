package commons;

import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * OrderQuestion data structure - describes a match question.
 */
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("ORDER")
public class OrderQuestion extends Question {

    /**
     * A boolean indicating whether the answer should be in increasing order.
     */
    public boolean increasing;

    public OrderQuestion(Question q, boolean increasing) {
        super(q);
        this.increasing = increasing;
    }

    @Override
    public boolean checkAnswer(List<Activity> userAnswer) {
        if (userAnswer.size() != activities.size()) {
            return false;
        }
        // Check if the order of answers' costs is correct
        int currentVal = userAnswer.get(0).cost;
        if (increasing) {
            for (int idx = 1; idx < activities.size(); idx++) {
                int newVal = userAnswer.get(idx).cost;
                if (newVal < currentVal) {
                    return false;
                }
                currentVal = newVal;
            }
        } else {
            for (int idx = 1; idx < activities.size(); idx++) {
                int newVal = userAnswer.get(idx).cost;
                if (newVal > currentVal) {
                    return false;
                }
                currentVal = newVal;
            }
        }
        return true;
    }
}

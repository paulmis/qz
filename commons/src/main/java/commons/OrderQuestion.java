package commons;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * OrderQuestion data structure - describes a match question.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class OrderQuestion extends Question {

    /**
     * A boolean indicating whether the answer should be in increasing order.
     */
    public boolean increasing = true;

    public OrderQuestion(Question q, boolean increasing) {
        super(q);
        this.increasing = increasing;
    }

    @Override
    public List<Double> checkAnswer(List<Answer> userAnswers) {
        List<Double> points = new ArrayList<>();
        for (Answer ans : userAnswers) {
            if (ans.getUserChoice().size() != activities.size()) {
                points.add(0.0);
                continue;
            }
            // Check if the order of answers' costs is correct
            int currentVal = ans.getUserChoice().get(0).getCost();
            double currentPoints = 0;
            double pointStep = 1.0 / (activities.size() - 1);
            if (increasing) {
                for (int idx = 1; idx < activities.size(); idx++) {
                    int newVal = ans.getUserChoice().get(idx).getCost();
                    if (newVal < currentVal) {
                        continue;
                    }
                    currentVal = newVal;
                    currentPoints += pointStep;
                }
            } else {
                for (int idx = 1; idx < activities.size(); idx++) {
                    int newVal = ans.getUserChoice().get(idx).getCost();
                    if (newVal > currentVal) {
                        continue;
                    }
                    currentVal = newVal;
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

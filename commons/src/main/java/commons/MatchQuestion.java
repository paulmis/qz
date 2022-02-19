package commons;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * MatchQuestion data structure - describes a match question.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("MATCH")
public class MatchQuestion extends Question {

    @Override
    public List<Double> checkAnswer(List<Answer> userAnswers) {
        List<Double> points = new ArrayList<>();
        for (Answer ans : userAnswers) {
            if (ans.getUserChoice().size() != activities.size()) {
                points.add(0.0);
                continue;
            }
            // Check if the order of answers corresponds to the order of questions
            double currentPoints = 0;
            double pointStep = 1.0 / (activities.size() - 1);
            for (int idx = 0; idx < activities.size(); idx++) {
                if (activities.get(idx).getCost() == ans.getUserChoice().get(idx).getCost()) {
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

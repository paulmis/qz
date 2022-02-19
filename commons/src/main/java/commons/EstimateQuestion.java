package commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * EstimateQuestion data structure - describes an estimate question.
 */
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("ESTIMATE")
public class EstimateQuestion extends Question {

    @Override
    public List<Double> checkAnswer(List<Answer> userAnswers) {
        List<Double> points = new ArrayList<>();
        List<Integer> errors = new ArrayList<>();
        Set<Integer> sortedErrors = new TreeSet<>();

        int target = activities.get(0).getCost();
        for (Answer ans : userAnswers) {
            points.add(0.0);
            if (ans.getUserChoice().size() != 1) {
                errors.add(Integer.MAX_VALUE);
                continue;
            }
            int userError = Math.abs(ans.getUserChoice().get(0).getCost() - target);
            errors.add(userError);
            sortedErrors.add(userError);
        }

        double pointStep = 1.0 / (sortedErrors.size() - 1);
        for (int myError : errors) {
            double currentPoints = 1;
            for (int err : sortedErrors) {
                if (myError == err) {
                    break;
                }
                currentPoints -= pointStep;
            }
            if (currentPoints - pointStep < 0) {
                currentPoints = 0;
            }
            points.add(currentPoints);
        }

        return points;
    }
}

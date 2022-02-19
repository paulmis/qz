package commons;

import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * MatchQuestion data structure - describes a match question.
 */
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("MATCH")
public class MatchQuestion extends Question {

    @Override
    public boolean checkAnswer(List<Activity> userAnswer) {
        if (userAnswer.size() != activities.size()) {
            return false;
        }
        // Check if the order of answers corresponds to the order of questions
        for (int idx = 0; idx < activities.size(); idx++) {
            if (!activities.get(idx).equals(userAnswer.get(idx))) {
                return false;
            }
        }
        return true;
    }
}

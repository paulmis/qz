package server.database.entities.question;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

/**
 * MCQuestion data structure - describes a multiple choice question.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
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
    public MCQuestion(Question q, Activity answer, boolean guessConsumption) {
        super(q);
        this.answer = answer;
        this.guessConsumption = guessConsumption;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        MCQuestion that = (MCQuestion) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

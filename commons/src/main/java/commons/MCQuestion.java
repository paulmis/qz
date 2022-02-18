package commons;

import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * MCQuestion data structure - describes a multiple choice question.
 */
@Entity
@DiscriminatorValue("MC")
public class MCQuestion extends Question {

    @ManyToOne
    @JoinColumn(name = "answer_id")
    public Activity answer;
    // indicates whether the user is asked to guess the energy consumption given the activity or vice versa
    public boolean guessConsumption;

    @SuppressWarnings("unused")
    private MCQuestion() {
        // for object mapper
    }

    /**
     * Constructor for the Multiple Choice Question class.
     *
     * @param activities       list of activities that produce the question.
     * @param text             text of the question.
     * @param answer           activity corresponding to the correct answer.
     * @param guessConsumption boolean to indicate if the user has to guess the energy consumption
     *                         or the corresponding activity.
     */
    public MCQuestion(List<Activity> activities, String text, Activity answer, boolean guessConsumption) {
        super(activities, text);
        this.answer = answer;
        this.guessConsumption = guessConsumption;
    }

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

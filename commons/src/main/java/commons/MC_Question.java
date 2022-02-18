package commons;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * MC_Question data structure - describes a multiple choice question.
 */
@Entity
@DiscriminatorValue("MC")
public class MC_Question extends Question {

    @ManyToOne
    @JoinColumn(name = "answer_id")
    public Activity answer;
    // indicates whether the user is asked to guess the energy consumption given the activity or vice versa
    public boolean guessConsumption;

    @SuppressWarnings("unused")
    private MC_Question() {
        // for object mapper
    }

    public MC_Question(List<Activity> activities, String text, Activity answer, boolean guessConsumption) {
        super(activities, text);
        this.answer = answer;
        this.guessConsumption = guessConsumption;
    }

    MC_Question(Question Q, Activity answer, boolean guessConsumption){
        super(Q);
        this.answer = answer;
        this.guessConsumption = guessConsumption;
    }

    @Override
    public boolean CheckAnswer(List<Activity> userAnswer) {
        // There should be a single answer in this list
        if(userAnswer.size() != 1){
            return false;
        }
        /*
        NB! This assumes the offered options will always have different cost values.
        Otherwise, a user might select the correct value while it is associated to a different activity
         */
        return answer.equals(userAnswer.get(0));
    }
}

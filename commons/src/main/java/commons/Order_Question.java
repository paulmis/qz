package commons;

import javax.persistence.Entity;
import java.util.List;

/**
 * Match_Question data structure - describes a match question.
 */
@Entity
public class Order_Question extends Question {

    public boolean increasing;

    @SuppressWarnings("unused")
    private Order_Question() {
        // for object mapper
    }

    public Order_Question(Question Q, boolean increasing) {
        super(Q);
        this.increasing = increasing;
    }

    public Order_Question(List<Activity> activities, String text, boolean increasing) {
        super(activities, text);
        this.increasing = increasing;
    }

    @Override
    public boolean CheckAnswer(List<Activity> userAnswer) {
        if(userAnswer.size() != activities.size()){
            return false;
        }
        // Check if the order of answers' costs is correct
        int currentVal = userAnswer.get(0).cost;
        if(increasing){
            for(int idx = 1; idx < activities.size(); idx++){
                int newVal = userAnswer.get(idx).cost;
                if(newVal < currentVal){
                    return false;
                }
                currentVal = newVal;
            }
        } else{
            for(int idx = 1; idx < activities.size(); idx++){
                int newVal = userAnswer.get(idx).cost;
                if(newVal > currentVal){
                    return false;
                }
                currentVal = newVal;
            }
        }
        return true;
    }
}

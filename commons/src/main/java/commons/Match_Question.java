package commons;

import javax.persistence.Entity;
import java.util.List;

/**
 * Match_Question data structure - describes a match question.
 */
@Entity
public class Match_Question extends Question {
    @SuppressWarnings("unused")
    private Match_Question() {
        // for object mapper
    }

    @Override
    public boolean CheckAnswer(List<Activity> userAnswer) {
        if(userAnswer.size() != activities.size()){
            return false;
        }
        // Check if the order of answers corresponds to the order of questions
        for(int idx = 0; idx < activities.size(); idx++){
            if(!activities.get(idx).equals(userAnswer.get(idx))){
                return false;
            }
        }
        return true;
    }
}

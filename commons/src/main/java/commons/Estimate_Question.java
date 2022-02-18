package commons;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Estimate_Question data structure - describes an estimate question.
 */
@Entity
public class Estimate_Question extends Question {

    @SuppressWarnings("unused")
    private Estimate_Question() {
        // for object mapper
    }

    @Override
    public boolean CheckAnswer(List<Activity> userAnswer) {
        // It doesn't make sense in this case, use GetRanking instead
        return false;
    }

    public int[] GetRanking(int[] guesses, int[] remainingTimes){
        // NB remainingTimes is how many seconds were left to answer the question
        List<Integer> ranking = new ArrayList<>();
        for(int idx = 0; idx < ranking.size(); idx++){
            ranking.set(idx, idx);
        }

        // NB highest ranking user is in index 0
        ranking.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                int goal = activities.get(0).cost;
                int est1 = Math.abs(guesses[o1] - goal);
                int est2 = Math.abs(guesses[o2] - goal);
                if(est1 == est2){
                    // answer time is tiebreaker
                    // the longer the remaining time the higher the ranking
                    return remainingTimes[o2] - remainingTimes[o1];
                }
                // the closer the estimate the higher the ranking
                return est1-est2;
            }
        });
        return ranking.stream().mapToInt(i->i).toArray();
    }
}

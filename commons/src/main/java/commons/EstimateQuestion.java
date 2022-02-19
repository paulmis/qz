package commons;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
    public boolean checkAnswer(List<Activity> userAnswer) {
        // It doesn't make sense in this case, use getRanking instead
        return false;
    }

    /**
     * getRanking: returns the list of users from the closest to the right guess to the furthest.
     *
     * @param guesses        list of guesses from each user.
     * @param remainingTimes seconds remaining to answer for each user.
     * @return array of integers, in position 0 the index of the highest ranking user.
     */
    public int[] getRanking(int[] guesses, int[] remainingTimes) {
        // NB remainingTimes is how many seconds were left to answer the question
        List<Integer> ranking = new ArrayList<>();
        for (int idx = 0; idx < ranking.size(); idx++) {
            ranking.set(idx, idx);
        }

        // NB highest ranking user is in index 0
        ranking.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                int goal = activities.get(0).cost;
                int est1 = Math.abs(guesses[o1] - goal);
                int est2 = Math.abs(guesses[o2] - goal);
                if (est1 == est2) {
                    // answer time is tiebreaker
                    // the longer the remaining time the higher the ranking
                    return remainingTimes[o2] - remainingTimes[o1];
                }
                // the closer the estimate the higher the ranking
                return est1 - est2;
            }
        });
        return ranking.stream().mapToInt(i -> i).toArray();
    }
}

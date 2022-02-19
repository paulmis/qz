package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class EstimateQuestionTest {

    private Activity getActivity(int cost) {
        Activity a = new Activity();
        a.setDescription("Activity of cost " + cost);
        a.setCost(cost);
        return a;
    }

    private Question getDefaultQuestion() {
        List<Activity> components = new ArrayList<>();
        Activity a = getActivity(100);
        components.add(a);
        Question myQ = new EstimateQuestion();
        myQ.setActivities(components);
        return myQ;
    }

    private Answer getAnswer(int estimate) {
        Answer ans = new Answer();
        List<Activity> answerActivities = new ArrayList<>();
        answerActivities.add(getActivity(estimate));
        ans.setUserChoice(answerActivities);
        return ans;
    }

    @Test
    void checkAnswerTest() {
        List<Answer> userGuesses = new ArrayList<>();
        userGuesses.add(getAnswer(90)); // #2
        userGuesses.add(getAnswer(50)); // #4
        userGuesses.add(getAnswer(107)); // #1
        userGuesses.add(getAnswer(0)); // #5
        userGuesses.add(getAnswer(75)); // #3

        Question q = getDefaultQuestion();
        assertEquals(new ArrayList<>(Arrays.asList(0.75, 0.25, 1.0, 0.0, 0.5)), q.checkAnswer(userGuesses));
    }

    @Test
    void checkAnswerSameRankTest() {
        List<Answer> userGuesses = new ArrayList<>();
        userGuesses.add(getAnswer(90)); // #2
        userGuesses.add(getAnswer(50)); // #3
        userGuesses.add(getAnswer(107)); // #1
        userGuesses.add(getAnswer(0)); // #4
        userGuesses.add(getAnswer(150)); // #3
        userGuesses.add(getAnswer(800)); // #5

        Question q = getDefaultQuestion();
        assertEquals(new ArrayList<>(Arrays.asList(0.75, 0.5, 1.0, 0.25, 0.5, 0.0)), q.checkAnswer(userGuesses));
    }

    @Test
    void checkAnswerMismatchingSize() {
        List<Answer> userGuesses = new ArrayList<>();
        userGuesses.add(getAnswer(90)); // #2
        userGuesses.add(getAnswer(50)); // #3
        userGuesses.add(getAnswer(107)); // #1
        userGuesses.add(getAnswer(0)); // #4

        List<Activity> answerAct = new ArrayList<>();
        answerAct.add(getActivity(0));
        answerAct.add(getActivity(1));
        answerAct.add(getActivity(2));
        answerAct.add(getActivity(3));
        Answer a = new Answer();
        a.setUserChoice(answerAct);
        userGuesses.add(a);

        Question q = getDefaultQuestion();
        assertThrows(IllegalArgumentException.class, () -> {
            q.checkAnswer(userGuesses);
        });
    }

    @Test
    void checkAnswerNullInput() {
        Question q = getDefaultQuestion();
        assertThrows(IllegalArgumentException.class, () -> {
            q.checkAnswer(null);
        });
    }
}
package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class MCQuestionTest {

    private Activity getActivity(int id) {
        Activity a = new Activity();
        a.setDescription("Activity" + (id + 1));
        a.setCost(2 + id * 4);
        return a;
    }

    private Question getDefaultQuestion() {
        List<Activity> components = new ArrayList<>();
        Activity a;
        for (int idx = 0; idx < 4; idx++) {
            a = getActivity(idx);
            components.add(a);
        }
        Question myQ = new MCQuestion();
        myQ.setActivities(components);
        ((MCQuestion) myQ).setAnswer(components.get(1));
        return myQ;
    }

    @Test
    void checkAnswerTest() {
        Question q = getDefaultQuestion();
        Activity a;
        List<Activity> answerActivities;
        Answer ans;
        List<Answer> userAnswers = new ArrayList<>();
        for (int idx = 0; idx < 6; idx++) {
            answerActivities = new ArrayList<>();
            int choice = idx % 4;
            a = getActivity(choice);
            answerActivities.add(a);
            ans = new Answer();
            ans.setUserChoice(answerActivities);
            userAnswers.add(ans);
        }

        List<Double> points = q.checkAnswer(userAnswers);
        assertEquals(new ArrayList<>(Arrays.asList(0.0, 1.0, 0.0, 0.0, 0.0, 1.0)), points);
    }

    @Test
    void checkAnswerMultipleAnswers() {
        Question q = getDefaultQuestion();
        Activity a;
        List<Activity> answerActivities;
        Answer ans;
        List<Answer> userAnswers = new ArrayList<>();
        for (int idx = 0; idx < 6; idx++) {
            answerActivities = new ArrayList<>();
            int choice = idx % 4;
            a = getActivity(choice);
            answerActivities.add(a);
            if (idx == 2) {
                a = getActivity(12);
                answerActivities.add(a);
            }
            ans = new Answer();
            ans.setUserChoice(answerActivities);
            userAnswers.add(ans);
        }

        assertThrows(IllegalArgumentException.class, () -> {
            q.checkAnswer(userAnswers);
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
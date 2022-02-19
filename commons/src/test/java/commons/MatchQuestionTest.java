package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class MatchQuestionTest {

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
        Question myQ = new MatchQuestion();
        myQ.setActivities(components);
        return myQ;
    }

    @Test
    void checkAnswerTest() {
        // first user has all correct
        List<Activity> answerAct = new ArrayList<>();
        answerAct.add(getActivity(0));
        answerAct.add(getActivity(1));
        answerAct.add(getActivity(2));
        answerAct.add(getActivity(3));
        Answer a = new Answer();
        a.setUserChoice(answerAct);
        List<Answer> userAnswers = new ArrayList<>();
        userAnswers.add(a);

        // second user has all wrong
        answerAct = new ArrayList<>();
        answerAct.add(getActivity(3));
        answerAct.add(getActivity(2));
        answerAct.add(getActivity(1));
        answerAct.add(getActivity(0));
        a = new Answer();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        // third user has two switched (2/4 of points)
        answerAct = new ArrayList<>();
        answerAct.add(getActivity(0));
        answerAct.add(getActivity(2));
        answerAct.add(getActivity(1));
        answerAct.add(getActivity(3));
        a = new Answer();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        Question q = getDefaultQuestion();
        assertEquals(new ArrayList<>(Arrays.asList(1.0, 0.0, 2.0 / 4)), q.checkAnswer(userAnswers));
    }

    @Test
    void checkAnswerMismatchingSize() {
        // first user has 4 activities
        List<Activity> answerAct = new ArrayList<>();
        answerAct.add(getActivity(0));
        answerAct.add(getActivity(1));
        answerAct.add(getActivity(2));
        answerAct.add(getActivity(3));
        Answer a = new Answer();
        a.setUserChoice(answerAct);
        List<Answer> userAnswers = new ArrayList<>();
        userAnswers.add(a);

        // second user has 5 activities
        answerAct = new ArrayList<>();
        answerAct.add(getActivity(3));
        answerAct.add(getActivity(2));
        answerAct.add(getActivity(1));
        answerAct.add(getActivity(0));
        answerAct.add(getActivity(12));
        a = new Answer();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        // third user has 4 activities
        answerAct = new ArrayList<>();
        answerAct.add(getActivity(0));
        answerAct.add(getActivity(2));
        answerAct.add(getActivity(1));
        answerAct.add(getActivity(3));
        a = new Answer();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        Question q = getDefaultQuestion();
        assertThrows(IllegalArgumentException.class, () -> {
            q.checkAnswer(null);
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
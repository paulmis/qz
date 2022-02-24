package server.database.entities.question;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MatchQuestionTest {

    static Question q;

    private static Activity getActivity(int id) {
        Activity a = new Activity();
        a.setDescription("Activity" + (id + 1));
        a.setCost(2 + id * 4);
        return a;
    }

    @BeforeAll
    static void defaultQuestion() {
        List<Activity> components = new ArrayList<>();
        Activity a;
        for (int idx = 0; idx < 4; idx++) {
            a = getActivity(idx);
            components.add(a);
        }
        q = new MatchQuestion();
        q.setActivities(components);
    }

    @Test
    void checkAnswerTest() {
        // first user has all correct
        List<Activity> answerAct = new ArrayList<>(List.of(
                getActivity(0), getActivity(1), getActivity(2), getActivity(3)));
        Answer a = new Answer();
        a.setUserChoice(answerAct);
        List<Answer> userAnswers = new ArrayList<>();
        userAnswers.add(a);

        // second user has all wrong
        answerAct = new ArrayList<>(List.of(
                getActivity(3), getActivity(2), getActivity(1), getActivity(0)));
        a = new Answer();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        // third user has two switched (2/4 of points)
        answerAct = new ArrayList<>(List.of(
                getActivity(0), getActivity(2), getActivity(1), getActivity(3)));
        a = new Answer();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        assertEquals(new ArrayList<>(List.of(1.0, 0.0, 2.0 / 4)), q.checkAnswer(userAnswers));
    }

    @Test
    void checkAnswerMismatchingSize() {
        // first user has 4 activities
        List<Activity> answerAct = new ArrayList<>(List.of(
                getActivity(0), getActivity(1), getActivity(2), getActivity(3)));
        Answer a = new Answer();
        a.setUserChoice(answerAct);
        List<Answer> userAnswers = new ArrayList<>();
        userAnswers.add(a);

        // second user has 5 activities
        answerAct = new ArrayList<>(List.of(
                getActivity(3), getActivity(2), getActivity(1), getActivity(0), getActivity(12)));
        a = new Answer();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        // third user has 4 activities
        answerAct = new ArrayList<>(List.of(
                getActivity(0), getActivity(2), getActivity(1), getActivity(3)));
        a = new Answer();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        assertThrows(IllegalArgumentException.class, () -> {
            q.checkAnswer(null);
        });
    }

    @Test
    void checkAnswerNullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            q.checkAnswer(null);
        });
    }

    @Test
    void allArgsConstructorTest() {
        // Test setup
        String questionText = "aQuestion";
        List<Activity> activities = new ArrayList<>(List.of(
                getActivity(0), getActivity(1), getActivity(2), getActivity(3)));
        UUID anId = UUID.randomUUID();
        Question matchAllArgs = new MatchQuestion(anId, activities, questionText);
        Question matchNoArgs = new MatchQuestion();
        matchNoArgs.setId(anId);
        matchNoArgs.setActivities(List.copyOf(activities));
        matchNoArgs.setText(questionText);

        // Constructor comparison
        assertNotNull(matchAllArgs);
        assertEquals(matchNoArgs.getId(), matchAllArgs.getId());
        assertEquals(matchNoArgs.getActivities(), matchAllArgs.getActivities());
        assertEquals(matchNoArgs.getText(), matchAllArgs.getText());
    }

    @Test
    void copyConstructorTest() {
        // Test setup
        String questionText = "aQuestion";
        List<Activity> activities = new ArrayList<>(List.of(
                getActivity(0), getActivity(1), getActivity(2), getActivity(3)));
        UUID anId = UUID.randomUUID();
        Question matchAllArgs = new MatchQuestion(anId, activities, questionText);
        Question matchCopy = new MatchQuestion(matchAllArgs);

        // Constructor comparison
        assertNotNull(matchCopy);
        assertEquals(matchAllArgs.getId(), matchCopy.getId());
        assertEquals(matchAllArgs.getActivities(), matchCopy.getActivities());
        assertEquals(matchAllArgs.getText(), matchCopy.getText());
    }
}
package server.database.entities.question;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static server.TestHelpers.getUUID;

import commons.entities.QuestionDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.database.entities.Answer;

class MCQuestionTest {

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
        q = new MCQuestion();
        q.setActivities(components);
        ((MCQuestion) q).setAnswer(components.get(1));
    }

    @Test
    void testFromDTOConstructor() {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setText("Question text");

        MCQuestion q = new MCQuestion(questionDTO);
        assertEquals("Question text", q.getText());
    }

    @Test
    void getRightAnswerTest() {
        int rightAnswerIdx = 2;
        ((MCQuestion) q).setAnswer(q.getActivities().get(rightAnswerIdx));
        Answer rightAnswer = new Answer();
        rightAnswer.setResponse(new ArrayList<>(List.of(q.getActivities().get(rightAnswerIdx))));
        assertEquals(rightAnswer, q.getRightAnswer());
    }

    @Test
    void checkAnswerTest() {
        List<Answer> userAnswers = new ArrayList<>();
        for (int idx = 0; idx < 6; idx++) {
            List<Activity> answerActivities = new ArrayList<>();
            int choice = idx % 4;
            Activity a = getActivity(choice);
            answerActivities.add(a);
            Answer ans = new Answer();
            ans.setResponse(answerActivities);
            userAnswers.add(ans);
        }

        assertEquals(new ArrayList<>(Arrays.asList(0.0, 1.0, 0.0, 0.0, 0.0, 1.0)), q.checkAnswer(userAnswers));
    }

    @Test
    void checkAnswerMultipleAnswers() {
        List<Answer> userAnswers = new ArrayList<>();
        for (int idx = 0; idx < 6; idx++) {
            List<Activity> answerActivities = new ArrayList<>();
            int choice = idx % 4;
            Activity a = getActivity(choice);
            answerActivities.add(a);
            if (idx == 2) {
                a = getActivity(12);
                answerActivities.add(a);
            }
            Answer ans = new Answer();
            ans.setResponse(answerActivities);
            userAnswers.add(ans);
        }

        assertThrows(IllegalArgumentException.class, () -> {
            q.checkAnswer(userAnswers);
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
        Question mcNoArgs = new MCQuestion();
        mcNoArgs.setId(getUUID(0));
        List<Activity> activities = new ArrayList<>(List.of(
                getActivity(0), getActivity(1), getActivity(2), getActivity(3)));
        mcNoArgs.setActivities(List.copyOf(activities));
        String questionText = "aQuestion";
        mcNoArgs.setText(questionText);
        Activity answer = getActivity(2);
        ((MCQuestion) mcNoArgs).setAnswer(answer);
        boolean guessConsumption = true;
        ((MCQuestion) mcNoArgs).setGuessConsumption(guessConsumption);
        Question mcAllArgs = new MCQuestion(getUUID(0), activities, questionText, answer, guessConsumption);

        // Constructor comparison
        assertEquals(mcNoArgs.getId(), mcAllArgs.getId());
        assertEquals(mcNoArgs.getActivities(), mcAllArgs.getActivities());
        assertEquals(mcNoArgs.getText(), mcAllArgs.getText());
        assertEquals(((MCQuestion) mcNoArgs).getAnswer(), ((MCQuestion) mcAllArgs).getAnswer());
        assertEquals(((MCQuestion) mcNoArgs).isGuessConsumption(), ((MCQuestion) mcAllArgs).isGuessConsumption());
    }

    @Test
    void copyConstructorTest() {
        // Test setup
        String questionText = "aQuestion";
        List<Activity> activities = new ArrayList<>(List.of(
                getActivity(0), getActivity(1), getActivity(2), getActivity(3)));
        Activity answer = getActivity(2);
        boolean guessConsumption = true;
        Question mcAllArgs = new MCQuestion(getUUID(0), activities, questionText, answer, guessConsumption);
        Question mcCopy = new MCQuestion(mcAllArgs, answer, guessConsumption);

        // Constructor comparison
        assertEquals(mcAllArgs.getId(), mcCopy.getId());
        assertEquals(mcAllArgs.getActivities(), mcCopy.getActivities());
        assertEquals(mcAllArgs.getText(), mcCopy.getText());
        assertEquals(((MCQuestion) mcAllArgs).getAnswer(), ((MCQuestion) mcCopy).getAnswer());
        assertEquals(((MCQuestion) mcAllArgs).isGuessConsumption(), ((MCQuestion) mcCopy).isGuessConsumption());
    }
}
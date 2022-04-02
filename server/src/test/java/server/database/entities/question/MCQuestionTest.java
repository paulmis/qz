package server.database.entities.question;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static server.utils.TestHelpers.getUUID;

import commons.entities.ActivityDTO;
import commons.entities.AnswerDTO;
import commons.entities.questions.MCQuestionDTO;
import java.util.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.services.answer.AnswerCollection;

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
        MCQuestionDTO questionDTO = new MCQuestionDTO(false, "");
        questionDTO.setText("Question text");

        MCQuestion q = new MCQuestion(questionDTO);
        assertEquals("Question text", q.getText());
    }

    @Test
    void getRightAnswerTest() {
        int rightAnswerIdx = 2;
        ((MCQuestion) q).setAnswer(q.getActivities().get(rightAnswerIdx));
        AnswerDTO rightAnswer = new AnswerDTO();
        rightAnswer.setResponse(new ArrayList<>(List.of(q.getActivities().get(rightAnswerIdx).getDTO())));
        assertEquals(rightAnswer, q.getRightAnswer());
    }

    @Test
    void checkAnswerTest() {
        AnswerCollection userAnswers = new AnswerCollection();
        for (int idx = 0; idx < 6; idx++) {
            List<ActivityDTO> answerActivities = new ArrayList<>();
            int choice = idx % 4;
            answerActivities.add(getActivity(choice).getDTO());
            AnswerDTO ans = new AnswerDTO();
            ans.setResponse(answerActivities);
            userAnswers.addAnswer(getUUID(idx), ans);
        }

        Map<UUID, Double> expectedScores = new HashMap<>();
        expectedScores.put(getUUID(0), 0.0);
        expectedScores.put(getUUID(1), 1.0);
        expectedScores.put(getUUID(2), 0.0);
        expectedScores.put(getUUID(3), 0.0);
        expectedScores.put(getUUID(4), 0.0);
        expectedScores.put(getUUID(5), 1.0);

        assertEquals(expectedScores, q.checkAnswer(userAnswers));
    }

    @Test
    void checkAnswerMultipleAnswers() {
        AnswerCollection userAnswers = new AnswerCollection();
        for (int idx = 0; idx < 6; idx++) {
            List<ActivityDTO> answerActivities = new ArrayList<>();
            int choice = idx % 4;
            ActivityDTO a = getActivity(choice).getDTO();
            answerActivities.add(a);
            if (idx == 1) {
                a = getActivity(12).getDTO();
                answerActivities.add(a);
            }
            AnswerDTO ans = new AnswerDTO();
            ans.setResponse(answerActivities);
            userAnswers.addAnswer(getUUID(idx), ans);
        }

        Map<UUID, Double> expectedScores = new HashMap<>();
        expectedScores.put(getUUID(0), 0.0);
        expectedScores.put(getUUID(1), 0.0);
        expectedScores.put(getUUID(2), 0.0);
        expectedScores.put(getUUID(3), 0.0);
        expectedScores.put(getUUID(4), 0.0);
        expectedScores.put(getUUID(5), 1.0);

        assertEquals(expectedScores, q.checkAnswer(userAnswers));
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
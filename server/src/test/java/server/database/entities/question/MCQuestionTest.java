package server.database.entities.question;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import commons.entities.ActivityDTO;
import commons.entities.AnswerDTO;
import commons.entities.QuestionDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
        AnswerDTO rightAnswer = new AnswerDTO();
        rightAnswer.setUserChoice(new ArrayList<>(List.of(q.getActivities().get(rightAnswerIdx).getDTO())));
        assertEquals(rightAnswer, q.getRightAnswer());
    }

    @Test
    void checkAnswerTest() {
        List<AnswerDTO> userAnswers = new ArrayList<>();
        for (int idx = 0; idx < 6; idx++) {
            List<ActivityDTO> answerActivities = new ArrayList<>();
            int choice = idx % 4;
            ActivityDTO a = getActivity(choice).getDTO();
            answerActivities.add(a);
            AnswerDTO ans = new AnswerDTO();
            ans.setUserChoice(answerActivities);
            userAnswers.add(ans);
        }

        assertEquals(new ArrayList<>(Arrays.asList(0.0, 1.0, 0.0, 0.0, 0.0, 1.0)), q.checkAnswer(userAnswers));
    }

    @Test
    void checkAnswerMultipleAnswers() {
        List<AnswerDTO> userAnswers = new ArrayList<>();
        for (int idx = 0; idx < 6; idx++) {
            List<ActivityDTO> answerActivities = new ArrayList<>();
            int choice = idx % 4;
            Activity a = getActivity(choice);
            answerActivities.add(a.getDTO());
            if (idx == 2) {
                a = getActivity(12);
                answerActivities.add(a.getDTO());
            }
            AnswerDTO ans = new AnswerDTO();
            ans.setUserChoice(answerActivities);
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
        UUID anId = UUID.randomUUID();
        mcNoArgs.setId(anId);
        List<Activity> activities = new ArrayList<>(List.of(
                getActivity(0), getActivity(1), getActivity(2), getActivity(3)));
        mcNoArgs.setActivities(List.copyOf(activities));
        String questionText = "aQuestion";
        mcNoArgs.setText(questionText);
        Activity answer = getActivity(2);
        ((MCQuestion) mcNoArgs).setAnswer(answer);
        boolean guessConsumption = true;
        ((MCQuestion) mcNoArgs).setGuessConsumption(guessConsumption);
        Question mcAllArgs = new MCQuestion(anId, activities, questionText, answer, guessConsumption);

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
        UUID anId = UUID.randomUUID();
        Activity answer = getActivity(2);
        boolean guessConsumption = true;
        Question mcAllArgs = new MCQuestion(anId, activities, questionText, answer, guessConsumption);
        Question mcCopy = new MCQuestion(mcAllArgs, answer, guessConsumption);

        // Constructor comparison
        assertEquals(mcAllArgs.getId(), mcCopy.getId());
        assertEquals(mcAllArgs.getActivities(), mcCopy.getActivities());
        assertEquals(mcAllArgs.getText(), mcCopy.getText());
        assertEquals(((MCQuestion) mcAllArgs).getAnswer(), ((MCQuestion) mcCopy).getAnswer());
        assertEquals(((MCQuestion) mcAllArgs).isGuessConsumption(), ((MCQuestion) mcCopy).isGuessConsumption());
    }
}